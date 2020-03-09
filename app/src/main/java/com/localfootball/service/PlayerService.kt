package com.localfootball.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.localfootball.client.PlayerClient
import com.localfootball.configuration.HttpConfiguration
import com.localfootball.database.AppRepository
import com.localfootball.exception.SomethingWentWrongException
import com.localfootball.exception.UnavailableNickOrEmailException
import com.localfootball.model.RegistrationRequest
import com.localfootball.model.UnavailableNickOrEmailResponse
import com.localfootball.util.localDateSerializer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PlayerService {
    private companion object {
        val userDao = AppRepository.database.userDao()
        val playerClient: PlayerClient = Retrofit.Builder()
            .baseUrl(HttpConfiguration.BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .localDateSerializer()
                        .create()
                )
            )
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(PlayerClient::class.java)
    }

    fun register(registrationRequest: RegistrationRequest) =
        playerClient.register(registrationRequest).map {
            if (it.code() == 406) {
                Gson().fromJson(
                    it.errorBody()?.charStream(),
                    UnavailableNickOrEmailResponse::class.java
                ).let { unavailableNickOrEmailResponse ->
                    throw UnavailableNickOrEmailException(
                        nicknameAvailable = unavailableNickOrEmailResponse.nicknameAvailable,
                        emailAvailable = unavailableNickOrEmailResponse.emailAvailable
                    )
                }
            }
            //temporary
            if (it.code() >= 400) {
                throw SomethingWentWrongException("Error code >= 400")
            }
        }

    fun getLoggedUser() =
        userDao.findFirstUser()

}