package com.github.alexxxdev.hrm.core

interface IHRM {
    fun getOSType(): OSType
    fun getCPUModel(): String
    fun getGPUModel(): String
    fun getData(): List<Result<HRMModel>>
}
