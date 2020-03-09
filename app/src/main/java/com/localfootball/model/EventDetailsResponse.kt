package com.localfootball.model

import java.time.LocalDateTime

data class EventDetailsResponse (
    val playground: Playground,
    val name: String,
    val description: String,
    val maxPlayers: Int,
    val startAt: LocalDateTime,
    val participations: List<Participation>
)