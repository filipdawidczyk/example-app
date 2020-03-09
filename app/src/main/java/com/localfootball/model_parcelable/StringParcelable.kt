package com.localfootball.model_parcelable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StringParcelable(
    val string: String
) : Parcelable