package com.localfootball.model_parcelable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IntParcelable (
    val int: Int
): Parcelable