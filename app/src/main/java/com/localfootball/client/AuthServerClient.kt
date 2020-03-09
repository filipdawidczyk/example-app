package com.localfootball.client

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthServerClient {
    @FormUrlEncoded
    @POST("/auth/realms/local-football/protocol/openid-connect/token")
    fun getToken(
        @Field("username") email: String,
        @Field("password") password: String,
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("client_secret") clientSecret: String
    ): Observable<Response<AccessTokenResponse>>
}

data class AccessTokenResponse(
    @field: SerializedName("access_token")
    val token: String
)