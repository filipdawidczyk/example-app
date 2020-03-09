package com.localfootball.model

import java.time.LocalDate
import java.time.LocalTime
import java.util.*

data class EventInfoWindowContent (
    val eventId: UUID,
    val name: String,
    val organizerName: String,
    val startDate: LocalDate,
    val startTime: LocalTime,
    val participantsNumber: Int,
    val maxPlayers: Int
)