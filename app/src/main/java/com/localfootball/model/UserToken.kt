package com.localfootball.model

import java.util.*

data class UserToken (
    val token: String,
    val userId: UUID
)