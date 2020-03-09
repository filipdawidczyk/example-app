package com.localfootball.client

import com.localfootball.model.*
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*
import java.util.*

const val EVENTS = "/api/events/"
const val PARTICIPATIONS = "/api/events/participations"

interface EventClient {
    @POST(EVENTS)
    fun createEvent(
        @Body createEventRequest: CreateEventRequest
    ): Single<Response<Any>>

    @GET(EVENTS)
    fun findEventsByRadius(
        @Query("radius") radius: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Observable<Response<List<MapEventResponse>>>

    @GET(PARTICIPATIONS)
    fun getPlayerParticipations(
        @Query("playerId") playerId: UUID
    ): Observable<Response<List<YourEventResponse>>>

    @GET("$EVENTS{id}")
    fun getEventById(@Path("id") id: UUID): Observable<Response<EventDetailsResponse>>

    //temporary feature
    @POST("$EVENTS{id}/participations")
    fun joinPlayerToEvent(@Path("id") eventId: UUID, @Body eventJoin: EventJoin): Single<Response<Void>>

}