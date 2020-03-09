package com.localfootball.database.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun findFirstUser(): Single<User>

    @Insert
    fun insert(user: User): Completable

    @Query("DELETE FROM users")
    fun removeAll(): Completable
}