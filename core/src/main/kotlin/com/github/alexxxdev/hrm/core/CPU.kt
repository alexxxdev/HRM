package com.github.alexxxdev.hrm.core

data class CPU(
    val name: String = "",
    val load: Float = 0f,
    val temperature: Float = 0f,
    val fan: Int = 0
)
