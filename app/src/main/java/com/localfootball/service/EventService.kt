package com.localfootball.service

import com.google.gson.GsonBuilder
import com.localfootball.client.EventClient
import com.localfootball.client.util.AuthenticatedOkHttpClient
import com.localfootball.configuration.HttpConfiguration.BASE_URL
import com.localfootball.exception.SomethingWentWrongException
import com.localfootball.model.*
import com.localfootball.util.localDateDeserializer
import com.localfootball.util.localDateTimeDeserializer
import com.localfootball.util.localDateTimeSerializer
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class EventService {
    private companion object {
        val eventClient: EventClient = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .localDateTimeSerializer()
                        .localDateTimeDeserializer()
                        .localDateDeserializer()
                        .create()
                )
            )
            .client(AuthenticatedOkHttpClient.client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(EventClient::class.java)

        private val playerService = PlayerService()
    }

    fun createEvent(createEventRequest: CreateEventRequest): Completable =
        eventClient.createEvent(createEventRequest)
            .map { it.body()!! }
            .ignoreElement()

    fun findEventsByRadius(
        radius: Int,
        latitude: Double,
        longitude: Double
    ): Observable<List<MapEventResponse>> =
        eventClient.findEventsByRadius(radius, latitude, longitude)
            .map { it.body()!! }

    fun getParticipationsOfLoggedPlayer(): Observable<List<YourEventResponse>> =
        playerService.getLoggedUser().flatMapObservable {
            eventClient.getPlayerParticipations(it.id).map { participations ->
                participations.body()
            }
        }

    fun getEventById(id: UUID): Observable<Response<EventDetailsResponse>> =
        eventClient.getEventById(id)

    fun joinPlayerToEvent(eventId: UUID): Completable =
        playerService.getLoggedUser().flatMapCompletable { user ->
            eventClient.joinPlayerToEvent(eventId, EventJoin(user.id))
                .observeOn(mainThread())
                .doOnSuccess {
                    //temporary
                    if (it.code() >= 400) {
                        throw SomethingWentWrongException("Jesteś już uczestnikiem wydarznia albo coś innego poszło nie tak :)")
                    }
                }.ignoreElement()
        }

}