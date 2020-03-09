package com.localfootball.client.util

import com.localfootball.configuration.HttpConfiguration.AUTHORIZATION_HEADER_NAME
import com.localfootball.configuration.HttpConfiguration.AUTHORIZATION_HEADER_PREFIX
import com.localfootball.service.LogoutService
import com.localfootball.service.PlayerService
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationTokenHeaderInterceptor : Interceptor {
    companion object {
        val playerService = PlayerService()
        val logoutService = LogoutService()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return playerService.getLoggedUser()
            .map {
                request.newBuilder().addHeader(
                    AUTHORIZATION_HEADER_NAME,
                    "$AUTHORIZATION_HEADER_PREFIX ${it.token}"
                ).build()
            }.map {req ->
                chain.proceed(req).also {
                    if (it.code() == 401) {
                        logoutService.logout()
                    }
                }
            }.blockingGet()
    }

}