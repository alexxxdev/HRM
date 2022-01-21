package com.github.alexxxdev.hrm.client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val weather: WeatherConfig = WeatherConfig(),
    val fullscreen: Boolean = false,
    @SerialName("server_ip")
    val serverIP: String = "",
    @SerialName("server_port")
    val serverPort: Int = 0,
    @SerialName("delay_in_second")
    val delay: Long = 1000
)

@Serializable
data class WeatherConfig(
    @SerialName("yandex_token")
    val token: String = "",
    val lang: String = "ru_RU",
    val lat: Double = 0.0,
    val lon: Double = 0.0
)
