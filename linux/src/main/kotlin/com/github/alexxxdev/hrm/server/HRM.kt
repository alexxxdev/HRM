package com.github.alexxxdev.hrm.server

import com.github.alexxxdev.hrm.core.CPU
import com.github.alexxxdev.hrm.core.GPU
import com.github.alexxxdev.hrm.core.HRMModel
import com.github.alexxxdev.hrm.core.IHRM
import com.github.alexxxdev.hrm.core.IO
import com.github.alexxxdev.hrm.core.OS
import com.github.alexxxdev.hrm.core.OSType
import kotlin.math.roundToInt

class HRM : IHRM {
    val io = IO()
    var hrmModel = HRMModel()

    init {
        hrmModel = hrmModel.copy(os = OS(name = System.getProperty("os.name"), type = OSType.Linux))
        val cpuModel = getCPUModel()
        val gpuModel = getGPUModel()
        hrmModel = hrmModel.copy(cpu = CPU(name = cpuModel), gpu = GPU(name = gpuModel))
    }

    override fun getOSType(): OSType {
        return OSType.Linux
    }

    override fun getCPUModel(): String {
        return ""
    }

    override fun getGPUModel(): String {
        return ""
    }

    override fun getData(params: Map<String, String>): List<Result<HRMModel>> {
        return listOf(Result.failure(Exception()))
    }
}
