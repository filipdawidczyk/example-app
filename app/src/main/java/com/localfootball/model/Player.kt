package com.localfootball.model

import java.time.LocalDate
import java.util.*

data class Player (
    val id: UUID,
    val nickname: String,
    val birthDate: LocalDate,
    val gender: Gender
)
