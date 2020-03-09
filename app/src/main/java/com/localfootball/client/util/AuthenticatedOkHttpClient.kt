package com.localfootball.client.util

import okhttp3.OkHttpClient

class AuthenticatedOkHttpClient {
    companion object {
        private val interceptor =
            AuthorizationTokenHeaderInterceptor()
        val client: OkHttpClient =
            OkHttpClient().newBuilder()
                .addInterceptor(interceptor)
                .build()
    }
}