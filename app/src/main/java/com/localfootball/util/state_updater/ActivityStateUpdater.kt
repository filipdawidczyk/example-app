package com.localfootball.util.state_updater

import android.os.Parcelable

interface ActivityStateUpdater {
    fun updateActivityCustomState(key: String, parcelable: Parcelable)
}