package com.github.alexxxdev.hrm.core

data class OS(
    val type: OSType = OSType.Unknown,
    val name: String = ""
)

enum class OSType { Unknown, Linux, Windows }
