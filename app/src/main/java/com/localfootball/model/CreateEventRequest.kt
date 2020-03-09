package com.localfootball.model

import java.time.LocalDateTime
import java.util.*

data class CreateEventRequest(
    val organizerId: UUID,
    val name: String,
    val startAt: LocalDateTime,
    val playground: Playground,
    val maxPlayers: Int,
    val description: String
)