package com.github.alexxxdev.hrm.core

import kotlinx.serialization.Serializable

@Serializable
data class GPU(
    val name: String = "",
    val load: Float = 0f,
    val temperature: Float = 0f,
    val fan: Float = 0f,
    val usedMemory: Float = 0f
)
