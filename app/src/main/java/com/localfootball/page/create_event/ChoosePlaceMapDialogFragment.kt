package com.localfootball.page.create_event

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.localfootball.R
import com.localfootball.util.state_updater.ActivityStateUpdater

class ChoosePlaceMapDialogFragment(
    private val activityStateUpdater: ActivityStateUpdater
) : DialogFragment() {
    companion object {
        fun newInstance(activityStateUpdater: ActivityStateUpdater): ChoosePlaceMapDialogFragment {
            return ChoosePlaceMapDialogFragment(
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
        customView = activity!!.layoutInflater.inflate(R.layout.dialog_choose_place_map, null)
        val builder = AlertDialog.Builder(context!!)
            .setView(customView)
        return builder.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction()
            .add(
                R.id.choosePlaceMapDialogFrameLayout,
                ChoosePlaceMapFragment(
                    activityStateUpdater
                )
            )
            .commit()
    }


}