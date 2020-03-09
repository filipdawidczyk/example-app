package com.localfootball.service

import com.localfootball.client.OpenStreetMapClient
import com.localfootball.configuration.HttpConfiguration.OPEN_STREET_MAP_URL
import com.localfootball.exception.LocationNotSupportedException
import com.localfootball.exception.UnknownLocationException
import com.localfootball.model.LocationResponse
import io.reactivex.Observable
import io.reactivex.Observable.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private val SUPPORTED_COUNTRY_CODES = setOf("pl")

class LocationService {
    companion object {
        private val openStreetMapClient: OpenStreetMapClient = Retrofit.Builder()
            .baseUrl(OPEN_STREET_MAP_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(OpenStreetMapClient::class.java)
    }

    fun getAddress(latitude: Double, longitude: Double): Observable<LocationResponse> =
        openStreetMapClient.getAddress(latitude, longitude, "json")
            .flatMap { response ->
                response.body()?.address?.takeIf { it.countryCode != null  && it.city() != null }
                    ?.let { just(it) }
                    ?: empty()
            }
            .switchIfEmpty(error(UnknownLocationException()))
            .doOnNext { response ->
                response.countryCode?.let {
                    if (!SUPPORTED_COUNTRY_CODES.contains(it)) throw LocationNotSupportedException()
                }
            }
            .map {
                LocationResponse(
                    street = it.street(),
                    city = it.city()!!,
                    countryCode = it.countryCode!!
                )
            }
}