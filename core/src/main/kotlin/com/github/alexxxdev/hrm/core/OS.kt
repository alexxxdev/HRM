package com.github.alexxxdev.hrm.core

import kotlinx.serialization.Serializable

@Serializable
data class OS(
    val type: OSType = OSType.Unknown,
    val name: String = ""
)

enum class OSType { Unknown, Linux, Windows }
