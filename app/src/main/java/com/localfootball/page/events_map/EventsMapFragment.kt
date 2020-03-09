package com.localfootball.page.events_map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.fragment.app.Fragment
import com.getbase.floatingactionbutton.FloatingActionButton
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.google.android.gms.common.ConnectionResult.SUCCESS
import com.google.android.gms.common.GoogleApiAvailability.getInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions.loadRawResourceStyle
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterManager
import com.localfootball.R
import com.localfootball.model.EventInfoWindowContent
import com.localfootball.model.MapEventResponse
import com.localfootball.model_parcelable.UUIDParcelable
import com.localfootball.page.create_event.CreateEventActivity
import com.localfootball.page.event_preview.EventPreviewActivity
import com.localfootball.page.your_events.YourEventsActivity
import com.localfootball.service.EventService
import com.localfootball.util.EVENT_UUID
import com.localfootball.util.setSafeOnClickListener
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import java.util.stream.Collectors

private const val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
val eventInfoHashMap = HashMap<Marker, MapEventClusterMarker>()

class EventsMapFragment : Fragment(), OnMapReadyCallback {

    private val eventService = EventService()
    private var firstTime = true

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mapEventClusterManager: ClusterManager<MapEventClusterMarker>
    private lateinit var clusterManagerRenderer: MapEventClusterManagerRenderer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_events_map, container, false)
        configureExpandableFloatingActionButtonMenu(view)
        setupMap()
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        configureGoogleMapFeatures(googleMap)
        configureFusedLocationProviderClient()
        configureMapStateManager(googleMap)
        fetchLastLocation()
        findEventsByRadius()
    }

    private fun configureMapStateManager(googleMap: GoogleMap) {
        val mapStateManager = MapStateManager(requireContext())
        val position = mapStateManager.savedCameraPosition
        if (position != null && !firstTime) {
            val update = newCameraPosition(position)
            googleMap.moveCamera(update)
            googleMap.mapType = mapStateManager.savedMapType
        }
    }

    private fun configureGoogleMapFeatures(googleMap: GoogleMap) {
        googleMap.setMapStyle(loadRawResourceStyle(requireContext(), R.raw.google_map_style))
        googleMap.isBuildingsEnabled = true
    }

    private fun configureFusedLocationProviderClient() {
        if (isGooglePlayServicesAvailable()) {
            fusedLocationProviderClient = getFusedLocationProviderClient(requireActivity())
        }
    }

    @SuppressLint("CheckResult")
    private fun findEventsByRadius() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            eventService.findEventsByRadius(
                100,
                location.latitude,
                location.longitude
            )
                .subscribeOn(io())
                .unsubscribeOn(io())
                .observeOn(mainThread())
                .subscribe(
                    {
                        pinClusterMarkers(it)
                    },
                    {
                        makeText(context, it.message, LENGTH_LONG).show()
                    }
                )
        }
    }

    private fun pinClusterMarkers(mapEventResponseList: List<MapEventResponse>) {
        val avatar = R.drawable.green_flat_ball_icon

        mapEventClusterManager = ClusterManager(activity?.applicationContext, googleMap)
        clusterManagerRenderer =
            MapEventClusterManagerRenderer(
                requireContext(),
                googleMap,
                mapEventClusterManager
            )
        mapEventClusterManager.renderer = clusterManagerRenderer

        mapEventResponseList.stream().map { mapEventResponse ->
            MapEventClusterMarker(
                LatLng(
                    mapEventResponse.location.latitude,
                    mapEventResponse.location.longitude
                ),
                EventInfoWindowContent(
                    eventId = mapEventResponse.id,
                    name = mapEventResponse.name,
                    organizerName = mapEventResponse.organizerNickname,
                    startDate = mapEventResponse.startAt.toLocalDate(),
                    startTime = mapEventResponse.startAt.toLocalTime(),
                    participantsNumber = mapEventResponse.participantsNumber,
                    maxPlayers = mapEventResponse.maxPlayers
                ),
                avatar
            )
        }.also { clusterMarkersStream ->
            clusterMarkersStream.collect(Collectors.toList())
                .forEach { clusterMarker ->
                    mapEventClusterManager.addItem(clusterMarker)
                }
        }
        mapEventClusterManager.cluster()
        configureInfoWindowFeatures()
    }

    private fun configureInfoWindowFeatures() {
        googleMap.setInfoWindowAdapter(
            MapEventMarkerInfoWindowAdapter(
                requireContext()
            )
        )
        googleMap.setOnInfoWindowClickListener {
            Intent(requireContext(), EventPreviewActivity::class.java)
                .putExtra(
                    EVENT_UUID,
                    UUIDParcelable(eventInfoHashMap[it]?.getEventInfoWindowContent()!!.eventId)
                ).let(::startActivity)
        }
    }

    private fun setupMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.eventsMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun fetchLastLocation() {
        checkPermissions()
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (firstTime) {
                setCameraToCurrentPlayerLocation(it)
                firstTime = false
            }
        }
    }

    private fun checkPermissions() {
        if (checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) != PERMISSION_GRANTED && checkSelfPermission(
                requireContext(),
                ACCESS_COARSE_LOCATION
            ) != PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            requestPermissions(
                requireActivity(),
                arrayOf(ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }
    }

    private fun setCameraToCurrentPlayerLocation(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(13f)
            .tilt(90f)
            .build()
        googleMap.moveCamera(newCameraPosition(cameraPosition))
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(context)
        if (SUCCESS == status)
            return true
        else {
            if (googleApiAvailability.isUserResolvableError(status))
                makeText(
                    context,
                    "Please Install google play services to use this application",
                    LENGTH_LONG
                ).show()
        }
        return false
    }

    override fun onPause() {
        super.onPause()
        MapStateManager(requireContext()).saveMapState(googleMap)
    }

    override fun onResume() {
        super.onResume()
        setupMap()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventInfoHashMap.clear()
    }

    private fun configureExpandableFloatingActionButtonMenu(view: View) {
        view.findViewById<FloatingActionButton>(R.id.eventsMapCreateEventOptionMenu)
            ?.setSafeOnClickListener {
                Intent(requireContext(), CreateEventActivity::class.java).let(::startActivity)
                view.findViewById<FloatingActionsMenu>(R.id.eventsMapFloatingActionMenu)
                    .collapse()
            }

        view.findViewById<FloatingActionButton>(R.id.eventsMapYourEventsOptionMenu)
            .setSafeOnClickListener {
                Intent(requireContext(), YourEventsActivity::class.java).let(::startActivity)
                view.findViewById<FloatingActionsMenu>(R.id.eventsMapFloatingActionMenu)
                    ?.collapse()
            }
    }
}