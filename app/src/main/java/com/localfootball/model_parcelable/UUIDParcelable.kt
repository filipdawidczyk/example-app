package com.localfootball.model_parcelable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class UUIDParcelable(
    val uuid: UUID
) : Parcelable