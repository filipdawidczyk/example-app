package com.localfootball.page.create_event

import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import com.localfootball.R
import com.localfootball.database.user.User
import com.localfootball.page.common.CustomDatePickerDialogFragment
import com.localfootball.model.CreateEventRequest
import com.localfootball.model.Playground
import com.localfootball.model_parcelable.*
import com.localfootball.service.AnimationService
import com.localfootball.service.EventService
import com.localfootball.service.PlayerService
import com.localfootball.service.VibrationService
import com.localfootball.util.DatePickerMode.EVENT_DATE
import com.localfootball.util.setSafeOnClickListener
import com.localfootball.util.state_updater.ActivityStateUpdater
import com.localfootball.util.state_updater.state_key.*
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.activity_create_event.*
import java.time.LocalDateTime

private const val COMPONENT_CLICK_ANIMATION_DURATION = 500L
private const val INITIAL_FONT_SIZE_VALUE = 12.0f
private const val INCREASED_FONT_SIZE_VALUE = 18.0f

class CreateEventActivity : AppCompatActivity(),
    ActivityStateUpdater {

    companion object {
        val createEventCurrentValuesState = Bundle()
        private val eventService = EventService()
        private val playerService = PlayerService()
        private val animationService = AnimationService()
        private val vibrationService = VibrationService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(createEventCurrentValuesState)
        setContentView(R.layout.activity_create_event)

        configureEventNameEditText()
        configureBackTextView()
        configureAnimationOfBallImageView()
        configureCountOfPlayersComponent()
        configureDateOfEventButton()
        configureStartTimeButton()
        configureChoosePlaceButton()
        configureCreateEventButton()
        configureDescriptionEditText()
        configureCreateEventPageEnterAnimations()
    }

    override fun onDestroy() {
        super.onDestroy()
        createEventCurrentValuesState.clear()
    }

    private fun updateMaxPlayersState(value: Int) {
        updateActivityCustomState(EVENT_MAX_PLAYERS_STATE_KEY, IntParcelable(value))
    }

    private fun configureEventNameEditText() {
        componentClickEffectClickListener(
            createEventPageNameEditText,
            createEventPageEventNameComponentConstraintLayout
        )
        editTextOnChangedListener(
            createEventPageNameEditText,
            EVENT_NAME_STATE_KEY
        )
    }

    private fun configureDescriptionEditText() {
        componentClickEffectClickListener(
            createEventPageDescriptionEditText,
            createEventPageDescriptionComponentConstraintLayout
        )
        editTextOnChangedListener(
            createEventPageDescriptionEditText,
            EVENT_DESCRIPTION_STATE_KEY
        )
    }

    private fun componentClickEffectClickListener(clickable: View, animated: View) {
        clickable.setSafeOnClickListener {
            vibrationService.clickVibration(applicationContext)
            animationService.fadeInAnimation(
                COMPONENT_CLICK_ANIMATION_DURATION,
                animated,
                0.1f,
                1.0f
            )
        }
        clickable.setOnFocusChangeListener { _, _ ->
            vibrationService.clickVibration(applicationContext)
            animationService.fadeInAnimation(
                COMPONENT_CLICK_ANIMATION_DURATION,
                animated,
                0.1f,
                1.0f
            )
        }
    }

    private fun editTextOnChangedListener(editText: EditText, savedStateElementName: String) =
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                handleTextSizeOfEditTextDependsLength(editText)
                updateEditTextState(editText, savedStateElementName)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    private fun updateEditTextState(editText: EditText, key: String) =
        createEventCurrentValuesState.putParcelable(
            key,
            StringParcelable(editText.text.toString())
        )

    private fun handleTextSizeOfEditTextDependsLength(editText: EditText) =
        if (editText.text.toString().toCharArray().isNotEmpty()) editText.textSize =
            INCREASED_FONT_SIZE_VALUE
        else editText.textSize =
            INITIAL_FONT_SIZE_VALUE

    private fun configureCreateEventButton() =
        createEventPageButton.setSafeOnClickListener {
            if (checkIfAllPropertiesAreChosen(listOfEventStates,
                    createEventCurrentValuesState
                )) {
                getCreatedEventRequest()
                    .flatMapCompletable(eventService::createEvent)
                    .subscribeOn(io())
                    .unsubscribeOn(io())
                    .observeOn(mainThread())
                    .subscribe(
                        {
                            makeText(
                                applicationContext,
                                getString(R.string.create_new_event_has_been_created),
                                LENGTH_LONG
                            ).show()
                            createEventCurrentValuesState.clear()
                            finish()
                        },
                        {
                            makeText(
                                applicationContext,
                                getString(R.string.create_new_event_has_not_been_created),
                                LENGTH_LONG
                            ).show()
                        })
            } else {
                makeText(applicationContext, getString(R.string.create_new_event_missing_fields_warn), LENGTH_LONG).show()
            }
        }

    private fun checkIfAllPropertiesAreChosen(checkList: List<String>, bundle: Bundle): Boolean {
        for (state in checkList) {
            if (!bundle.containsKey(state)) {
                return false
            }
        }
        return true
    }

    private fun getCreatedEventRequest() =
        playerService.getLoggedUser()
            .map(::eventRequest)

    private fun eventRequest(it: User): CreateEventRequest {
        return CreateEventRequest(
            organizerId = it.id,
            name = (createEventCurrentValuesState.get(EVENT_NAME_STATE_KEY) as StringParcelable?)!!.string,
            startAt = LocalDateTime.of(
                (createEventCurrentValuesState.get(EVENT_DATE_STATE_KEY) as LocalDateParcelable?)!!.localDate,
                (createEventCurrentValuesState.get(EVENT_START_TIME_STATE_KEY) as LocalTimeParcelable?)!!.localTime
            ),
            playground = getPlayground(),
            maxPlayers = (createEventCurrentValuesState.get(EVENT_MAX_PLAYERS_STATE_KEY) as IntParcelable?)!!.int,
            description = (createEventCurrentValuesState.get(EVENT_DESCRIPTION_STATE_KEY) as StringParcelable?)!!.string
        )
    }

    private fun getPlayground(): Playground {
        return Playground(
            latitude = (createEventCurrentValuesState.get(EVENT_LATLNG_STATE_KEY) as LocationDataParcelable?)!!.latitude,
            longitude = (createEventCurrentValuesState.get(EVENT_LATLNG_STATE_KEY) as LocationDataParcelable?)!!.longitude,
            street = (createEventCurrentValuesState.get(EVENT_LATLNG_STATE_KEY) as LocationDataParcelable?)!!.street,
            city = (createEventCurrentValuesState.get(EVENT_LATLNG_STATE_KEY) as LocationDataParcelable?)!!.city,
            countryCode = (createEventCurrentValuesState.get(EVENT_LATLNG_STATE_KEY) as LocationDataParcelable?)!!.countryCode.toUpperCase()
        )
    }

    private fun configureAnimationOfBallImageView() =
        animationService.startInfiniteMediumVelocityRotating(
            applicationContext,
            createEventPageSecondaryBallImageView
        )

    private fun configureBackTextView() =
        createEventPageBackTextView.setOnClickListener {
            finish()
        }

    private fun configureStartTimeButton() {
        createEventPageStartTimeButton.setSafeOnClickListener {
            vibrationService.clickVibration(applicationContext)
            animationService.fadeInAnimation(
                COMPONENT_CLICK_ANIMATION_DURATION,
                createEventPageStartTimeComponentConstraintLayout,
                0.1f,
                1.0f
            )
            TimePickerDialogFragment
                .newInstance(getString(R.string.create_new_event_start_time_doubledot), this)
                .show(supportFragmentManager, null)
        }
    }

    private fun configureDateOfEventButton() {
        createEventPageDateOfEventButton.setSafeOnClickListener {
            vibrationService.clickVibration(applicationContext)
            animationService.fadeInAnimation(
                COMPONENT_CLICK_ANIMATION_DURATION,
                createEventPageDateOfEventComponentConstraintLayout,
                0.1f,
                1.0f
            )
            CustomDatePickerDialogFragment
                .newInstance(EVENT_DATE, this)
                .show(supportFragmentManager, null)
        }
    }

    private fun configureChoosePlaceButton() {
        createEventPageLocationButton.setSafeOnClickListener {
            vibrationService.clickVibration(applicationContext)
            animationService.fadeInAnimation(
                COMPONENT_CLICK_ANIMATION_DURATION,
                createEventPageLocationComponentConstraintLayout,
                0.1f,
                1.0f
            )
            ChoosePlaceMapDialogFragment.newInstance(this)
                .show(supportFragmentManager, null)
        }
    }

    private fun configureCountOfPlayersComponent() {
        val listOfCountImageViews = listOf<ImageView>(
            countImageView0, countImageView1, countImageView2, countImageView3,
            countImageView4, countImageView5, countImageView6, countImageView7,
            countImageView8, countImageView9, countImageView10, countImageView11,
            countImageView12, countImageView13, countImageView14, countImageView15,
            countImageView16, countImageView17, countImageView18, countImageView19,
            countImageView20, countImageView21
        )

        listOfCountImageViews.forEach {
            it.alpha = 0.1f
        }

        countImageView0.alpha = 1.0f
        countImageView1.alpha = 1.0f

        createEventCurrentValuesState.putParcelable(
            EVENT_MAX_PLAYERS_STATE_KEY,
            IntParcelable(maxPlayersComponentNumberPicker.value)
        )

        maxPlayersComponentNumberPicker.setOnValueChangedListener { picker, _, newVal ->
            when (picker.value) {
                2 -> {
                    for (n in 0..1) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 2 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                3 -> {
                    for (n in 0..2) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 3 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                4 -> {
                    for (n in 0..3) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 4 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                5 -> {
                    for (n in 0..4) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 5 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                6 -> {
                    for (n in 0..5) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 6 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                7 -> {
                    for (n in 0..6) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 7 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                8 -> {
                    for (n in 0..7) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 8 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                9 -> {
                    for (n in 0..8) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 9 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                10 -> {
                    for (n in 0..9) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 10 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                11 -> {
                    for (n in 0..10) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 11 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                12 -> {
                    for (n in 0..11) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 12 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                13 -> {
                    for (n in 0..12) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 13 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                14 -> {
                    for (n in 0..13) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 14 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                15 -> {
                    for (n in 0..14) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 15 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                16 -> {
                    for (n in 0..15) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 16 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                17 -> {
                    for (n in 0..16) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 17 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                18 -> {
                    for (n in 0..17) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 18 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                19 -> {
                    for (n in 0..18) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 19 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                20 -> {
                    for (n in 0..19) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 20 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                21 -> {
                    for (n in 0..20) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 21 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
                22 -> {
                    for (n in 0..21) listOfCountImageViews[n].alpha = 1.0f
                    for (n in 22 until listOfCountImageViews.size) listOfCountImageViews[n].alpha =
                        0.1f
                }
            }
            updateMaxPlayersState(newVal)
        }
    }

    private fun configureCreateEventPageEnterAnimations() {
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            500,
            createEventPageTextView,
            0.0f,
            1.0f
        )
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            530,
            createEventPageEventNameComponentConstraintLayout,
            0.0f,
            1.0f
        )
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            560,
            createEventPageDateOfEventComponentConstraintLayout,
            0.0f,
            1.0f
        )
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            590,
            createEventPageStartTimeComponentConstraintLayout,
            0.0f,
            1.0f
        )
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            650,
            createEventPageLocationComponentConstraintLayout,
            0.0f,
            1.0f
        )
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            680,
            createEventPageMaxPlayersComponentConstraintLayout,
            0.0f,
            1.0f
        )
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            710,
            createEventPageDescriptionComponentConstraintLayout,
            0.0f,
            0.7f
        )
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            800,
            createEventPageButton,
            0.0f,
            1.0f
        )
    }

    override fun updateActivityCustomState(key: String, parcelable: Parcelable) {
        createEventCurrentValuesState.putParcelable(key, parcelable)
    }
}