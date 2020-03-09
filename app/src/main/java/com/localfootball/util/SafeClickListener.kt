package com.localfootball.util

import android.os.SystemClock
import android.view.View

private const val DEFAULT_INTERVAL: Int = 1000

class SafeClickListener(private val onSafeCLick: (View) -> Unit) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < DEFAULT_INTERVAL) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) =
    setOnClickListener(SafeClickListener {
        onSafeClick(it)
    })

