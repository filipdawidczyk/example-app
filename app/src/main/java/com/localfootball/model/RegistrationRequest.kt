package com.localfootball.model

import java.time.LocalDate

data class RegistrationRequest (
    val nickname: String,
    val email: String,
    val password: String,
    val birthDate: LocalDate,
    val gender: Gender
)
