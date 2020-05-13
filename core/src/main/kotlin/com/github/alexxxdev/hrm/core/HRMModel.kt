package com.github.alexxxdev.hrm.core

data class HRMModel(
    val os: OS = OS(),
    val cpu: CPU = CPU(),
    val gpu: GPU = GPU(),
    val memory: Memory = Memory()
)
