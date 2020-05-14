package com.github.alexxxdev.hrm.core

import kotlinx.serialization.Serializable

@Serializable
data class Memory(
    val used: Float = 0f,
    val total: Float = 0f
)
