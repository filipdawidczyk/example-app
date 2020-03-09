package com.localfootball.page.create_event

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color.BLACK
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult.SUCCESS
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.localfootball.R
import com.localfootball.exception.LocationNotSupportedException
import com.localfootball.exception.UnknownLocationException
import com.localfootball.model.LocationResponse
import com.localfootball.model_parcelable.LocationDataParcelable
import com.localfootball.service.LocationService
import com.localfootball.util.state_updater.state_key.EVENT_LATLNG_STATE_KEY
import com.localfootball.util.setSafeOnClickListener
import com.localfootball.util.state_updater.ActivityStateUpdater
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.fragment_choose_place_map.*

private const val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

class ChoosePlaceMapFragment(
    private val activityStateUpdater: ActivityStateUpdater
) : Fragment(), OnMapReadyCallback {

    private var location: LocationResponse? = null

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationService = LocationService()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_choose_place_map, container, false)
        setupMap()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureConfirmButton()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        configureGoogleMapFeatures(googleMap)
        checkIfGooglePlayServicesAvailable()
        fetchLastLocation()
    }

    override fun onResume() {
        super.onResume()
        setupMap()
    }

    private fun setupMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.choosePlaceMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun configureConfirmButton() {
        choosePlaceMapConfirmButton.setSafeOnClickListener {
            location?.let {
                updateLocationDataState(it)
                updateCreateEventLocationComponent()
                (parentFragment as DialogFragment).dismiss()
            } ?: makeText(requireContext(), getString(R.string.create_new_event_unknown_address), LENGTH_LONG).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateCreateEventLocationComponent() {
        activity?.findViewById<TextView>(R.id.createEventPageLocationLatTextView)?.text =
            """Lat: ${googleMap.cameraPosition.target.latitude}"""
        activity?.findViewById<TextView>(R.id.createEventPageLocationLngTextView)?.text =
            """Lng: ${googleMap.cameraPosition.target.longitude}"""
        activity?.findViewById<Button>(R.id.createEventPageLocationButton)?.text =
            choosePlaceMapLocationTextView.text
        activity?.findViewById<Button>(R.id.createEventPageLocationButton)?.text =
            choosePlaceMapLocationTextView.text
        activity?.findViewById<Button>(R.id.createEventPageLocationButton)?.textSize = 18.0f
        activity?.findViewById<Button>(R.id.createEventPageLocationButton)?.setTextColor(BLACK)
    }

    private fun updateLocationDataState(location: LocationResponse) {
        activityStateUpdater.updateActivityCustomState(
            EVENT_LATLNG_STATE_KEY,
            LocationDataParcelable(
                googleMap.cameraPosition.target.latitude,
                googleMap.cameraPosition.target.longitude,
                location.street,
                location.city,
                location.countryCode
            )
        )
    }

    private fun checkIfGooglePlayServicesAvailable() {
        if (isGooglePlayServicesAvailable()) {
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireContext())
        }
    }

    private fun configureGoogleMapFeatures(googleMap: GoogleMap) {
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isCompassEnabled = false
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.google_map_style
            )
        )
        googleMap.setOnCameraMoveListener {
            configurationOfMapCameraMoveListener()
        }
        googleMap.setOnCameraIdleListener {
            configurationOfMapCameraIdleListener()
        }
    }

    @SuppressLint("CheckResult")
    private fun configurationOfMapCameraIdleListener() {
        locationService.getAddress(
            googleMap.cameraPosition.target.latitude,
            googleMap.cameraPosition.target.longitude
        )
            .subscribeOn(io())
            .unsubscribeOn(io())
            .observeOn(mainThread())
            .subscribe(this::handleLocationResponse, this::handleError)
    }

    private fun handleLocationResponse(response: LocationResponse) {
        this.location = response

        choosePlaceMapLocationTextView.text = response.toUiString()
        choosePlaceMapLatLngTextView.text =
            "Lat: ${googleMap.cameraPosition.target.latitude} | Lng: ${googleMap.cameraPosition.target.longitude}"
    }

    private fun handleError(throwable: Throwable) {
        this.location = null
        choosePlaceMapLocationTextView.text = getString(R.string.create_new_event_unknown_address)
        when (throwable) {
            is LocationNotSupportedException -> getString(throwable.getErrorMessageResourceId())
            is UnknownLocationException -> null
            else -> "${getString(R.string.create_new_event_location_api_error)} ${throwable.message}"
        }?.let {
            makeText(
                context,
                it,
                LENGTH_LONG
            ).show()
        }
    }

    private fun LocationResponse.toUiString() =
        convertCountryCodeToCountryName(countryCode)
            .let { country ->
                street?.let { "$it | $city | $country" }
                    ?: "$city | $country"
            }

    private fun convertCountryCodeToCountryName(countryCode: String) =
        resources.getString(resources.getIdentifier(countryCode, "string", context!!.packageName))

    private fun configurationOfMapCameraMoveListener() {
        startChoosePlacePinAnimation()
        startDotAnimation()
    }

    private fun startChoosePlacePinAnimation() =
        choosePlaceMapPinImageView.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.pin_animation
            )
        )

    private fun startDotAnimation() {
        choosePlaceMapDotImageView.visibility = VISIBLE
        choosePlaceMapDotImageView.alpha = 1.0f
        choosePlaceMapDotImageView.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.dot_animation
            )
        )
    }

    private fun fetchLastLocation() {
        if (checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) != PERMISSION_GRANTED && checkSelfPermission(
                requireContext(),
                ACCESS_COARSE_LOCATION
            ) != PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            setCurrentLocation(it)
        }
    }

    private fun setCurrentLocation(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(17f)
            .tilt(90f)
            .build()
        googleMap.moveCamera(newCameraPosition(cameraPosition))
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(context)
        if (SUCCESS == status)
            return true
        else {
            if (googleApiAvailability.isUserResolvableError(status))
                makeText(
                    requireContext(),
                    "Please Install google play services to use this application",
                    LENGTH_LONG
                ).show()
        }
        return false
    }
}