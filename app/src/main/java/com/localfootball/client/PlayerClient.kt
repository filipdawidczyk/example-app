package com.localfootball.client

import com.localfootball.model.RegistrationRequest
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface PlayerClient {
    @POST("/api/players")
    fun register(@Body registrationRequest: RegistrationRequest): Observable<Response<Any>>

    @GET("/api/players/{playerId}/settings")
    fun getPlayerSettings(@Path("playerId") playerId: UUID): Single<Response<PlayerSettings>>

    @PUT("/api/players/{playerId}/settings")
    fun updatePlayerSettings(@Path("playerId") playerId: UUID, @Body update: PlayerSettingsUpdate): Single<Response<Any>>
}

data class PlayerSettings(
    val eventSearchRadius: Int
)

data class PlayerSettingsUpdate(
    val eventSearchRadius: Int? = null
)