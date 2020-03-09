package com.localfootball.model_parcelable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Parcelize
data class LocalDateParcelable (
    val localDate: LocalDate
) : Parcelable