package com.localfootball.database.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: UUID,
    @ColumnInfo
    val email: String,
    @ColumnInfo(name = "token")
    val token: String
)