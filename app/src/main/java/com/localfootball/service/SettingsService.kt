package com.localfootball.service

import com.localfootball.client.PlayerClient
import com.localfootball.client.PlayerSettingsUpdate
import com.localfootball.client.util.AuthenticatedOkHttpClient
import com.localfootball.configuration.HttpConfiguration.BASE_URL
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.*

class SettingsService {
    private companion object {
        val playerClient: PlayerClient = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(AuthenticatedOkHttpClient.client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(PlayerClient::class.java)
    }

    fun getPlayerSettings(playerId: UUID) =
        playerClient.getPlayerSettings(playerId)
            .map { it.body()!! }

    fun updatePlayerSettings(playerId: UUID, playerSettingsUpdate: PlayerSettingsUpdate) =
        playerClient.updatePlayerSettings(playerId, playerSettingsUpdate).ignoreElement()
}