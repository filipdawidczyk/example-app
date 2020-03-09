package com.localfootball.page.event_preview

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat.getFont
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.localfootball.R
import com.localfootball.exception.SomethingWentWrongException
import com.localfootball.model.EventDetailsResponse
import com.localfootball.model.Participation
import com.localfootball.model.PlayerEventRole
import com.localfootball.model_parcelable.UUIDParcelable
import com.localfootball.service.AnimationService
import com.localfootball.service.EventService
import com.localfootball.service.PlayerService
import com.localfootball.util.EVENT_UUID
import com.localfootball.util.setSafeOnClickListener
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.activity_event_preview.*

private const val MAP_ZOOM_VALUE = 16f
private const val PARTICIPANTS_COUNT_OF_DISPLAYED_COLUMNS = 2

class EventPreviewActivity : AppCompatActivity(), OnMapReadyCallback {

    private val animationService = AnimationService()
    private val eventService = EventService()
    private val playerService = PlayerService()

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_preview)
        configureAnimations()
        configureCollapsingToolbarLayout()
        fetchEvent()
        setupMap()
        configureItemDecoratorForParticipantsRecyclerView()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        configureGoogleMap(googleMap)
    }

    @SuppressLint("CheckResult")
    private fun fetchEvent() {
        eventService.getEventById(intent.getParcelableExtra<UUIDParcelable>(EVENT_UUID).uuid)
            .subscribeOn(io())
            .unsubscribeOn(io())
            .observeOn(mainThread())
            .subscribe(
                {response ->
                    loadEventDetailsDataToComponents(response.body()!!)
                    playerService.getLoggedUser()
                        .unsubscribeOn(io())
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribe({user ->
                            response.body()!!.participations.find { participation ->
                                participation.player.id == user.id
                            }.also {
                                configureLeaveEventButton()
                            } ?: also {
                                configureJoinToEventButton()
                            }
                        },{
                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                        })
                },
                {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            )
    }

    @SuppressLint("SetTextI18n")
    private fun loadEventDetailsDataToComponents(eventDetailsResponse: EventDetailsResponse) {
        eventPreviewPageCollapsingToolBarLayout.title = eventDetailsResponse.name
        configureEventPlayersRecyclerView(eventDetailsResponse.participations)
        setEventLocationOnMap(
            eventDetailsResponse.playground.latitude,
            eventDetailsResponse.playground.longitude
        )
        eventPreviewPageExpandedToolbarLocationTextView.text =
            "${eventDetailsResponse.playground.street} | ${eventDetailsResponse.playground.city}"
        eventPreviewPageDateValueTextView.text =
            eventDetailsResponse.startAt.toLocalDate().toString()
        eventPreviewPageStartTimeValueTextView.text =
            eventDetailsResponse.startAt.toLocalTime().toString()
        eventPreviewPagePlayersCountValueTextView.text =
            "${eventDetailsResponse.participations.size} / ${eventDetailsResponse.maxPlayers}"

        eventDetailsResponse.participations.find {
            it.playerRole == PlayerEventRole.ORGANIZER
        }.also {
            eventPreviewPageOrganizerValueTextView.text = it?.player?.nickname
        }

        eventPreviewPageDescriptionValueTextView.text = eventDetailsResponse.description
    }

    private fun setEventLocationOnMap(latitude: Double, longitude: Double) {
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(latitude, longitude))
            .zoom(MAP_ZOOM_VALUE)
            .build()
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.eventPreviewPageExpandedToolbarLocationMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun configureGoogleMap(googleMap: GoogleMap) {
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this,
                R.raw.google_map_style
            )
        )
        googleMap.isBuildingsEnabled = false
        googleMap.uiSettings.isScrollGesturesEnabled = false
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isCompassEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        googleMap.uiSettings.isRotateGesturesEnabled = false
        googleMap.uiSettings.isZoomGesturesEnabled = false
    }

    private fun configureCollapsingToolbarLayout() {
        eventPreviewPageCollapsingToolBarLayout?.setCollapsedTitleTypeface(
            getFont(
                applicationContext,
                R.font.bungee
            )
        )
        eventPreviewPageCollapsingToolBarLayout?.setExpandedTitleTypeface(
            getFont(
                applicationContext,
                R.font.bungee
            )
        )
    }

    private fun configureEventPlayersRecyclerView(participations: List<Participation>) {
        eventPreviewPageParticipantsRecyclerView.layoutManager =
            GridLayoutManager(this, PARTICIPANTS_COUNT_OF_DISPLAYED_COLUMNS)
        eventPreviewPageParticipantsRecyclerView.adapter =
            EventPlayersRecyclerViewAdapter(this, participations)
    }

    private fun configureItemDecoratorForParticipantsRecyclerView() {
        eventPreviewPageParticipantsRecyclerView.addItemDecoration(
            EventPlayersDecorator(
                resources.getDimensionPixelSize(R.dimen.event_players_recycler_vertical_spacing),
                resources.getDimensionPixelSize(R.dimen.event_players_recycler_horizontal_spacing)
            )
        )
    }

    private fun configureJoinToEventButton() {
        eventPreviewPageFooterInvitationRequestButton.text =
            getText(R.string.event_preview_join_to_event)
        eventPreviewPageFooterInvitationRequestButton.setSafeOnClickListener {
            //temporary
            eventService.joinPlayerToEvent(intent.getParcelableExtra<UUIDParcelable>(EVENT_UUID).uuid)
                .subscribeOn(io())
                .unsubscribeOn(io())
                .observeOn(mainThread())
                .subscribe(
                    {
                        Toast.makeText(this, "Dołączono do wydarzenia", Toast.LENGTH_LONG).show()
                        fetchEvent()
                    }, {
                        Toast.makeText(
                            this,
                            (it as SomethingWentWrongException).getCapturedErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
        }
    }

    private fun configureLeaveEventButton() {
        eventPreviewPageFooterTextView.text = " "
        eventPreviewPageFooterInvitationRequestButton.text =
            getText(R.string.event_preview_leave_event)
        eventPreviewPageFooterInvitationRequestButton.setSafeOnClickListener {
        }
    }

    private fun configureAnimations() {
        animationService.startInfiniteMediumVelocityRotating(
            this,
            eventPreviewPageCollapsedToolbarImage
        )
    }

}