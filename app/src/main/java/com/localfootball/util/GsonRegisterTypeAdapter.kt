package com.localfootball.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

fun GsonBuilder.localDateTimeSerializer(): GsonBuilder {
    return registerTypeAdapter(
            LocalDateTime::class.java,
            JsonSerializer<LocalDateTime> { src, _, _ ->
                JsonPrimitive(ISO_LOCAL_DATE_TIME.format(src))
            })
}

fun GsonBuilder.localDateTimeDeserializer(): GsonBuilder {
    return registerTypeAdapter(
        LocalDateTime::class.java,
        JsonDeserializer { src, _, _ ->
            LocalDateTime.parse(
                src.asJsonPrimitive.asString,
                ISO_LOCAL_DATE_TIME
            )
        })
}

fun GsonBuilder.localDateDeserializer(): GsonBuilder {
    return registerTypeAdapter(
        LocalDate::class.java,
        JsonDeserializer { src, _, _ ->
            LocalDate.parse(
                src.asJsonPrimitive.asString,
                ISO_LOCAL_DATE
            )
        })
}

fun GsonBuilder.localDateSerializer(): GsonBuilder {
    return registerTypeAdapter(
        LocalDate::class.java,
        JsonSerializer<LocalDate> { src, _, _ ->
            JsonPrimitive(ISO_LOCAL_DATE.format(src))
        })
}

