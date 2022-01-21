package com.github.alexxxdev.hrm.client

import com.github.alexxxdev.hrm.client.model.Config
import com.github.alexxxdev.hrm.client.model.WeatherConfig
import kotlinx.serialization.KSerializer

class ClientConfig(name: String) : com.github.alexxxdev.hrm.core.Config<Config>(name) {

    val weather: WeatherConfig
        get() = model.weather

    val fullscreen: Boolean
        get() = model.fullscreen

    val serverIP: String
        get() = model.serverIP

    val serverPort: Int
        get() = model.serverPort

    val delay: Long
        get() = model.delay

    override var model: Config = Config()

    override fun prefillConfig() = model

    override fun serializer(): KSerializer<Config> = Config.serializer()
}
