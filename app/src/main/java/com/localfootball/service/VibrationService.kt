package com.localfootball.service

import android.content.Context
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity

private const val CLICK_VIBRATION_TIME = 2L

class VibrationService {

    fun clickVibration(applicationContext: Context) {
        val vibrator = applicationContext.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                CLICK_VIBRATION_TIME,
                DEFAULT_AMPLITUDE
            )
        )
    }

}