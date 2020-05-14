package com.github.alexxxdev.hrm.server

import kotlinx.serialization.Serializable

@Serializable
data class ConfigModel(
    val ip: String = "",
    val port: Int = 2323,
    val refreshDataInterval: Long = 1000L,
    val params: Map<String, String> = emptyMap()
)
