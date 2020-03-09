package com.localfootball.service

import com.localfootball.client.AuthServerClient
import com.localfootball.configuration.HttpConfiguration.BASE_URL
import com.localfootball.model.UserToken
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

private const val CLIENT_SECRET = "702d4ace-939e-4298-81c5-3544294a4d51"
private const val CLIENT_ID = "local-football-android"
private const val GRANT_TYPE = "password"

class AuthServerService {
    private companion object {
        val authServerClient: AuthServerClient = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(AuthServerClient::class.java)
    }

    fun getToken(email: String, password: String) =
        authServerClient.getToken(
            email = email,
            password = password,
            clientId = CLIENT_ID,
            grantType = GRANT_TYPE,
            clientSecret = CLIENT_SECRET
        ).map {
            UserToken(
                token = it.body()?.token ?: throw RuntimeException("Brak tokena - zrób dedykowany exception"),
                userId = it.headers()["X-User"]?.let(UUID::fromString) ?: throw RuntimeException("Brak Usera w Hederach - zrób dedykowany exception")
            )
        }
}