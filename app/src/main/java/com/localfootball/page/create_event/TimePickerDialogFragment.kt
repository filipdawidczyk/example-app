package com.localfootball.page.create_event

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color.BLACK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.localfootball.R
import com.localfootball.model_parcelable.LocalTimeParcelable
import com.localfootball.util.state_updater.state_key.EVENT_START_TIME_STATE_KEY
import com.localfootball.util.setSafeOnClickListener
import com.localfootball.util.state_updater.ActivityStateUpdater
import kotlinx.android.synthetic.main.dialog_custom_time_picker.*
import java.time.LocalTime

private val hours24h = listOf(
    "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
    "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"
)

private val minutes = listOf(
    "00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"
)

private const val MIN_VALUE_OF_NUMBER_PICKER = 1

class TimePickerDialogFragment(
    private val label: String,
    private val activityStateUpdater: ActivityStateUpdater
) : DialogFragment() {

    private lateinit var customView: View

    companion object {
        fun newInstance(
            label: String,
            activityStateUpdater: ActivityStateUpdater
        ): TimePickerDialogFragment {
            return TimePickerDialogFragment(
                label,
                activityStateUpdater
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return customView
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        customView = activity!!.layoutInflater.inflate(R.layout.dialog_custom_time_picker, null)
        val builder = AlertDialog.Builder(context!!)
            .setView(customView)
        return builder.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureConfirmButton()
        configureCustomTimePickerLabel()

        configureCustomTimePickerHourNumberPicker()
        configureCustomTimePickerMinuteNumberPicker()

        val localTime =
            CreateEventActivity.createEventCurrentValuesState.getParcelable<LocalTimeParcelable?>(
                EVENT_START_TIME_STATE_KEY
            )?.localTime

        customTimePickerHourNumberPicker.value = hours24h.indexOf("""0${localTime?.hour}""")

    }

    private fun configureCustomTimePickerMinuteNumberPicker() {
        customTimePickerMinuteNumberPicker.minValue =
            MIN_VALUE_OF_NUMBER_PICKER
        customTimePickerMinuteNumberPicker.maxValue = minutes.size
        customTimePickerMinuteNumberPicker.displayedValues = minutes.toTypedArray()
    }

    private fun configureCustomTimePickerHourNumberPicker() {
        customTimePickerHourNumberPicker.minValue =
            MIN_VALUE_OF_NUMBER_PICKER
        customTimePickerHourNumberPicker.maxValue = hours24h.size
        customTimePickerHourNumberPicker.displayedValues = hours24h.toTypedArray()
    }

    private fun configureCustomTimePickerLabel() {
        customTimePickerLabelTextView.text = label
    }

    private fun configureConfirmButton() {
        customTimePickerConfirmButton.setSafeOnClickListener {
            updateStartTimeComponent()
            updateStartTimeState()
            dismiss()
        }
    }

    private fun updateStartTimeComponent() {
        activity?.findViewById<Button>(R.id.createEventPageStartTimeButton)?.text = getTimeAsString()
        activity?.findViewById<Button>(R.id.createEventPageStartTimeButton)?.textSize = 18.0f
        activity?.findViewById<Button>(R.id.createEventPageStartTimeButton)?.setTextColor(BLACK)
    }

    private fun updateStartTimeState() {
        activityStateUpdater.updateActivityCustomState(
            EVENT_START_TIME_STATE_KEY, LocalTimeParcelable(
                getTimeAsLocalTime()
            )
        )
    }

    private fun getTimeAsLocalTime(): LocalTime {
        return LocalTime.of(
            customTimePickerHourNumberPicker.displayedValues[customTimePickerHourNumberPicker.value - 1].toInt(),
            customTimePickerMinuteNumberPicker.displayedValues[customTimePickerMinuteNumberPicker.value - 1].toInt()
        )
    }

    private fun getTimeAsString(): String {
        val hourStringValue =
            customTimePickerHourNumberPicker.displayedValues[customTimePickerHourNumberPicker.value - 1]
        val minuteStringValue =
            customTimePickerMinuteNumberPicker.displayedValues[customTimePickerMinuteNumberPicker.value - 1]
        return "$hourStringValue : $minuteStringValue"
    }
}

