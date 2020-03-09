package com.localfootball.database

import androidx.room.TypeConverter
import java.util.*

class AppConverters {

    @TypeConverter
    fun fromString(string: String) = UUID.fromString(string)

    @TypeConverter
    fun uuidToString(uuid: UUID) = uuid.toString()
}