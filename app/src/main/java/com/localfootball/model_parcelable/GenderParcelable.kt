package com.localfootball.model_parcelable

import android.os.Parcelable
import com.localfootball.model.Gender
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GenderParcelable(
    val gender: Gender
) : Parcelable