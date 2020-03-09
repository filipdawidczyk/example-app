package com.localfootball.model

import java.time.LocalDateTime
import java.util.*

data class MapEventResponse (
    val id: UUID,
    val organizerNickname: String,
    val organizerId: UUID,
    val location: Location,
    val name: String,
    val participantsNumber: Int,
    val maxPlayers: Int,
    val startAt: LocalDateTime
)

data class Location(
    val latitude: Double,
    val longitude: Double
)