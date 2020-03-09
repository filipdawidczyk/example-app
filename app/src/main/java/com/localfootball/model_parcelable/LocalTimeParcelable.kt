package com.localfootball.model_parcelable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalTime

@Parcelize
data class LocalTimeParcelable (
    val localTime: LocalTime
) : Parcelable