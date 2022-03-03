package com.github.alexxxdev.hrm.server

import com.github.alexxxdev.hrm.core.CPU
import com.github.alexxxdev.hrm.core.GPU
import com.github.alexxxdev.hrm.core.HRMModel
import com.github.alexxxdev.hrm.core.IHRM
import com.github.alexxxdev.hrm.core.IO
import com.github.alexxxdev.hrm.core.Log
import com.github.alexxxdev.hrm.core.OS
import com.github.alexxxdev.hrm.core.OSType

class HRM : IHRM {
    private val io = IO()
    private var hrmModel = HRMModel()

    init {
        hrmModel = hrmModel.copy(
            os = OS(
                name = System.getProperty("os.name") + "(" + System.getProperty("os.version") + ")",
                type = OSType.Linux
            )
        )
        val cpuModel = getCPUModel()
        Log.d(cpuModel)
        val gpuModel = getGPUModel()
        Log.d(gpuModel)
        hrmModel = hrmModel.copy(cpu = CPU(name = cpuModel), gpu = GPU(name = gpuModel))
    }

    override fun getOSType(): OSType {
        return OSType.Linux
    }

    override fun getCPUModel(): String {
        return io.getShellOutput(
            arrayOf("/bin/sh", "-c", "cat /proc/cpuinfo | grep 'model name' | uniq")
        )
            .replace("\r", "")
            .replace("\n", "")
            .replaceFirst("model name", "")
            .replaceFirst(":", "")
            .trim()
    }

    override fun getGPUModel(): String {
        return io.getShellOutput(
            arrayOf("/bin/sh", "-c", "lshw -C display | grep product | uniq")
        )
            .split("\n")[0]
            .replaceBefore(":", "")
            .replace(":", "")
            .trim()
    }

    override fun getData(params: Map<String, String>): List<Result<HRMModel>> {
        return listOf(
            Result.success(
                hrmModel
            )
        )
    }
}
