package com.localfootball.model_parcelable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationDataParcelable(
    val latitude: Double,
    val longitude: Double,
    val street: String?,
    val city: String,
    val countryCode: String
):Parcelable