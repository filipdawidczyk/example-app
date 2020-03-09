package com.localfootball.client

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenStreetMapClient {
    @GET("/reverse")
    fun getAddress(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("format") format: String
    ): Observable<Response<OpenStreetMapReverseGeocodingResponse>>
}

data class OpenStreetMapReverseGeocodingResponse(
    val address: OpenStreetMapAddress?
)

data class OpenStreetMapAddress(
    @field: SerializedName("house_number")
    private val houseNumber: String?,
    private val road: String?,
    private val suburb: String?,
    private val village: String?,
    private val county: String?,
    private val town: String?,
    private val city: String?,
    @field: SerializedName("country_code")
    val countryCode: String?
) {
    fun street() = road?.let { road ->
        houseNumber?.let { "$road $it" } ?: road
    }

    fun city() = city ?: town ?: village ?: suburb
}