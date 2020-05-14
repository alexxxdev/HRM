package com.github.alexxxdev.hrm.core

import kotlinx.serialization.Serializable

@Serializable
data class HRMModel(
    val os: OS = OS(),
    val cpu: CPU = CPU(),
    val gpu: GPU = GPU(),
    val memory: Memory = Memory()
)
