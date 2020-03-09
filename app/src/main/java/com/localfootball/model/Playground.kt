package com.localfootball.model

data class Playground (
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val countryCode: String,
    val street: String? = null
)