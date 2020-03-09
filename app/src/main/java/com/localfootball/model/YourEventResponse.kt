package com.localfootball.model

import java.time.LocalDateTime
import java.util.*

data class YourEventResponse(
    val event: YourEvent,
    val playerRole: PlayerEventRole
)

data class YourEvent(
    val id: UUID,
    val organizerId: UUID,
    val address: Address,
    val name: String,
    val description: String,
    val participantsNumber: Int,
    val maxPlayers: Int,
    val startAt: LocalDateTime
)

