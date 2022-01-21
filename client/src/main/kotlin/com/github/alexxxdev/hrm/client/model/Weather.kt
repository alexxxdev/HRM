package com.github.alexxxdev.hrm.client.model

import kotlinx.serialization.Serializable

@Serializable
data class Weather(
    val now: Long,
    val now_dt: String,
    val fact: Fact
)
