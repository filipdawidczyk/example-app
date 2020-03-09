package com.localfootball.page.common

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color.BLACK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.localfootball.R
import com.localfootball.model_parcelable.LocalDateParcelable
import com.localfootball.util.DatePickerMode
import com.localfootball.util.DatePickerMode.BIRTH_DATE
import com.localfootball.util.DatePickerMode.EVENT_DATE
import com.localfootball.util.setSafeOnClickListener
import com.localfootball.util.state_updater.ActivityStateUpdater
import com.localfootball.util.state_updater.state_key.EVENT_DATE_STATE_KEY
import com.localfootball.util.state_updater.state_key.REGISTRATION_BIRTH_DATE_STATE_KEY
import kotlinx.android.synthetic.main.dialog_custom_date_picker.*
import java.time.LocalDate
import java.time.Month
import java.time.Month.DECEMBER
import java.time.Month.JANUARY
import java.time.Year
import java.time.YearMonth
import kotlin.math.min

private const val DEFAULT_MIN_DAY_VALUE = 1
private const val EVENT_CREATION_DATE_MONTH_MARGIN = 2L
private const val INCREASED_FONT_SIZE_VALUE_FOR_EVENT = 18.0f

class CustomDatePickerDialogFragment(
    private val datePickerMode: DatePickerMode,
    private val activityStateUpdater: ActivityStateUpdater
) : DialogFragment() {

    companion object {
        fun newInstance(
            datePickerMode: DatePickerMode,
            activityStateUpdater: ActivityStateUpdater
        ): CustomDatePickerDialogFragment {
            return CustomDatePickerDialogFragment(
                datePickerMode,
                activityStateUpdater
            )
        }
    }

    private lateinit var customView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return customView
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        customView = activity!!.layoutInflater.inflate(R.layout.dialog_custom_date_picker, null)
        val builder = AlertDialog.Builder(context!!)
            .setView(customView)
        return builder.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureBallImageView()
        configureCustomDatePicker()
    }

    private fun configureBallImageView() {
        customDatePickerSecondaryBallImageView1.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.infinite_medium_rotation
            )
        )

        customDatePickerSecondaryBallImageView2.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.infinite_medium_rotation
            )
        )
    }

    private fun configureCustomDatePicker() {
        when (datePickerMode) {
            BIRTH_DATE -> {
                configureBirthDateCustomDatePicker()
                configureConfirmButtonActionForBirthDate()
            }
            EVENT_DATE -> {
                configureEventDateCustomDatePicker()
                configureConfirmButtonActionForEventDate()
            }
        }
    }

    private fun configureBirthDateCustomDatePicker() {
        customDatePickerBirthDateTextView.text = getString(R.string.date_picker_birth_date)
        customDatePickerYearNumberPicker.minValue = 1950
        customDatePickerYearNumberPicker.maxValue = Year.now().value - 13
        customDatePickerYearNumberPicker.value = Year.now().value - 18
        customDatePickerMonthNumberPicker.minValue = 1
        customDatePickerMonthNumberPicker.maxValue = 12
        customDatePickerDayNumberPicker.minValue = 1
        customDatePickerDayNumberPicker.maxValue =
            YearMonth.of(
                customDatePickerYearNumberPicker.value,
                customDatePickerMonthNumberPicker.value
            ).lengthOfMonth()
        customDatePickerMonthNumberPicker.setOnValueChangedListener { picker, _, _ ->
            customDatePickerDayNumberPicker.maxValue =
                getLengthOfMonthDependsOfYearAndMonth(
                    customDatePickerYearNumberPicker.value,
                    picker.value
                )
        }
        customDatePickerYearNumberPicker.setOnValueChangedListener { picker, _, _ ->
            customDatePickerDayNumberPicker.maxValue =
                getLengthOfMonthDependsOfYearAndMonth(
                    picker.value,
                    customDatePickerMonthNumberPicker.value
                )
        }
    }

    private fun configureEventDateCustomDatePicker() {
        customDatePickerBirthDateTextView.text = getString(R.string.date_picker_event_date)
        initializeEventDateCustomDatePickerFields()
        setDatePickerFields()

        customDatePickerYearNumberPicker.setOnValueChangedListener{_ , _ , _ ->
            setMonthFields()
            setDayFields()
        }
        customDatePickerMonthNumberPicker.setOnValueChangedListener{_ , _ , _ ->
            setDayFields()
        }
    }

    private fun initializeEventDateCustomDatePickerFields() {
        val now = LocalDate.now()
        customDatePickerDayNumberPicker.minValue = 1
        customDatePickerDayNumberPicker.maxValue = now.lengthOfMonth()
        customDatePickerDayNumberPicker.value = now.dayOfMonth
        customDatePickerMonthNumberPicker.minValue = JANUARY.value
        customDatePickerMonthNumberPicker.maxValue = DECEMBER.value
        customDatePickerMonthNumberPicker.value = now.monthValue
        customDatePickerYearNumberPicker.minValue = now.year
        customDatePickerYearNumberPicker.maxValue = now.year
        customDatePickerYearNumberPicker.value = now.year
    }

    private fun setDatePickerFields() {
        setYearFields()
        setMonthFields()
        setDayFields()
    }

    private fun setDayFields() {
        val now = LocalDate.now()
        val nowPlusMargin = now.plusMonths(EVENT_CREATION_DATE_MONTH_MARGIN)
        customDatePickerDayNumberPicker.minValue =
            if (now.month == getSelectedDate().month)
                now.dayOfMonth
            else
                DEFAULT_MIN_DAY_VALUE

        customDatePickerDayNumberPicker.maxValue =
            if (nowPlusMargin.month == getSelectedDate().month)
                nowPlusMargin.dayOfMonth
            else
                getSelectedDate().lengthOfMonth()
        customDatePickerDayNumberPicker.value = customDatePickerDayNumberPicker.minValue
    }

    private fun setMonthFields(){
        val now = LocalDate.now()
        val nowPlusMargin = now.plusMonths(EVENT_CREATION_DATE_MONTH_MARGIN)

        customDatePickerMonthNumberPicker.minValue =
            if (getSelectedDate().year == now.year)
                now.monthValue
            else
                JANUARY.value
        customDatePickerMonthNumberPicker.maxValue =
            if (getSelectedDate().year == nowPlusMargin.year)
                nowPlusMargin.monthValue
            else
                DECEMBER.value
        customDatePickerMonthNumberPicker.value = customDatePickerMonthNumberPicker.minValue
    }

    private fun setYearFields() {
        val now = LocalDate.now()
        val nowPlusMargin = now.plusMonths(EVENT_CREATION_DATE_MONTH_MARGIN)

        customDatePickerYearNumberPicker.minValue = now.year
        customDatePickerYearNumberPicker.maxValue = nowPlusMargin.year
    }

    private fun getSelectedDate() = LocalDate.of(
        customDatePickerYearNumberPicker.value,
        customDatePickerMonthNumberPicker.value,
        min(
            customDatePickerDayNumberPicker.value,
            Month.of(customDatePickerMonthNumberPicker.value).maxLength()
        )
    )

    private fun configureConfirmButtonActionForBirthDate() {
        customDatePickerConfirmButton.setSafeOnClickListener {
            updateBirthDateComponent()
            updateBirthDateState()
            dismiss()
        }
    }

    private fun updateBirthDateComponent() {
        activity?.findViewById<Button>(R.id.registerPageBirthDateButton)?.text =
            getEventDateString()
        activity?.findViewById<Button>(R.id.registerPageBirthDateButton)?.setTextColor(BLACK)
    }

    private fun updateBirthDateState() {
        activityStateUpdater.updateActivityCustomState(
            REGISTRATION_BIRTH_DATE_STATE_KEY,
            LocalDateParcelable(getDateAsLocalDate())
        )
    }

    private fun configureConfirmButtonActionForEventDate() {
        customDatePickerConfirmButton.setSafeOnClickListener {
            updateEventDateComponent()
            updateEventDateState()
            dismiss()
        }
    }

    private fun updateEventDateState() {
        activityStateUpdater.updateActivityCustomState(
            EVENT_DATE_STATE_KEY,
            LocalDateParcelable(getDateAsLocalDate())
        )
    }

    private fun updateEventDateComponent() {
        activity?.findViewById<Button>(R.id.createEventPageDateOfEventButton)?.text =
            getEventDateString()
        activity?.findViewById<Button>(R.id.createEventPageDateOfEventButton)?.textSize =
            INCREASED_FONT_SIZE_VALUE_FOR_EVENT
        activity?.findViewById<Button>(R.id.createEventPageDateOfEventButton)?.setTextColor(BLACK)
    }

    private fun getDateAsLocalDate(): LocalDate {
        return LocalDate.of(
            customDatePickerYearNumberPicker.value,
            customDatePickerMonthNumberPicker.value,
            customDatePickerDayNumberPicker.value
        )
    }

    private fun getEventDateString(): String {
        val dayStringValue: String =
            if (customDatePickerDayNumberPicker.value < 10) {
                "0" + customDatePickerDayNumberPicker.value.toString()
            } else {
                customDatePickerDayNumberPicker.value.toString()
            }

        val monthStringValue: String =
            if (customDatePickerMonthNumberPicker.value < 10) {
                "0" + customDatePickerMonthNumberPicker.value.toString()
            } else {
                customDatePickerMonthNumberPicker.value.toString()
            }

        return customDatePickerYearNumberPicker.value.toString() + " - " +
                monthStringValue + " - " +
                dayStringValue
    }

    private fun getLengthOfMonthDependsOfYearAndMonth(
        yearPickerValue: Int,
        monthPickerValue: Int
    ): Int {
        return YearMonth.of(yearPickerValue, monthPickerValue).lengthOfMonth()
    }
}
