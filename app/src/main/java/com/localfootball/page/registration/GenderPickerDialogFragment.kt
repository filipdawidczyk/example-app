package com.localfootball.page.registration

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.localfootball.R
import com.localfootball.model.Gender
import com.localfootball.model.Gender.*
import com.localfootball.model_parcelable.GenderParcelable
import com.localfootball.util.setSafeOnClickListener
import com.localfootball.util.state_updater.ActivityStateUpdater
import kotlinx.android.synthetic.main.dialog_gender_picker.*

class GenderPickerDialogFragment(
    private val activityStateUpdater: ActivityStateUpdater
) : DialogFragment() {
    companion object {
        fun newInstance(activityStateUpdater: ActivityStateUpdater): GenderPickerDialogFragment {
            return GenderPickerDialogFragment(
                activityStateUpdater
            )
        }
    }

    lateinit var customView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return customView
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        customView = activity!!.layoutInflater.inflate(R.layout.dialog_gender_picker, null)
        val builder = AlertDialog.Builder(context!!)
            .setView(customView)
        return builder.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureMaleButton()
        configureFemaleButton()
        configureUnknownButton()
    }

    private fun configureMaleButton() {
        genderPickerDialogMaleButton.setSafeOnClickListener {
            activity?.findViewById<Button>(R.id.registerPageGenderButton)?.text =
                getString(R.string.male)
            updateGenderState(MALE)
            updateGenderComponent()
            dismiss()
        }
    }

    private fun configureFemaleButton() {
        genderPickerDialogFemaleButton.setSafeOnClickListener {
            activity?.findViewById<Button>(R.id.registerPageGenderButton)?.text =
                getString(R.string.female)
            updateGenderState(FEMALE)
            updateGenderComponent()
            dismiss()
        }
    }

    private fun configureUnknownButton() {
        genderPickerDialogUnknownButton.setSafeOnClickListener {
            activity?.findViewById<Button>(R.id.registerPageGenderButton)?.text =
                getString(R.string.gender_unknown)
            updateGenderState(NOT_SPECIFIED)
            updateGenderComponent()
            dismiss()
        }
    }

    private fun updateGenderState(gender: Gender) {
        activityStateUpdater.updateActivityCustomState(
            "gender",
            GenderParcelable(gender)
        )
    }

    private fun updateGenderComponent() {
        activity?.findViewById<Button>(R.id.registerPageGenderButton)?.setTextColor(Color.BLACK)
    }
}