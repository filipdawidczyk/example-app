package com.localfootball.database

import androidx.room.Room
import com.localfootball.MyApplication

class AppRepository {
    companion object {
        val database = Room.databaseBuilder(
            MyApplication.appContext,
            AppDatabase::class.java, "users"
        ).build()
    }
}