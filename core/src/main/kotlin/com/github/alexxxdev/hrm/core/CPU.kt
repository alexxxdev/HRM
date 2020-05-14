package com.github.alexxxdev.hrm.core

import kotlinx.serialization.Serializable

@Serializable
data class CPU(
    val name: String = "",
    val load: Float = 0f,
    val temperature: Float = 0f,
    val fan: Int = 0
)
