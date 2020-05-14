package com.github.alexxxdev.hrm.core

interface IHRM {
    fun getOSType(): OSType
    fun getCPUModel(): String
    fun getGPUModel(): String
    fun getData(params: Map<String, String>): List<Result<HRMModel>>
}
