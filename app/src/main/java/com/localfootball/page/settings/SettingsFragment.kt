package com.localfootball.page.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.localfootball.R
import com.localfootball.service.LogoutService
import com.localfootball.service.VibrationService
import com.localfootball.util.setSafeOnClickListener

class SettingsFragment : Fragment() {

    private val logoutService = LogoutService()
    private val vibrationService = VibrationService()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureFindRangeSeekBar(view)

        view.findViewById<ConstraintLayout>(R.id.logoutConstraintLayout).setSafeOnClickListener {
            vibrationService.clickVibration(requireContext())
            logoutService.logout()
        }
    }

    private fun configureFindRangeSeekBar(view: View) {
        view.findViewById<SeekBar>(R.id.seekBar2).min = 1
        view.findViewById<SeekBar>(R.id.seekBar2).max = 200
        view.findViewById<SeekBar>(R.id.seekBar2)
            .setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    view.findViewById<TextView>(R.id.textView11).text = "$progress km"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    vibrationService.clickVibration(requireContext())
                }
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    vibrationService.clickVibration(requireContext())
                }
            })
    }
}
