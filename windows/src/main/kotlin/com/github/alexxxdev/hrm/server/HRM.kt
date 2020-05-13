package com.github.alexxxdev.hrm.server

import com.github.alexxxdev.hrm.core.CPU
import com.github.alexxxdev.hrm.core.GPU
import com.github.alexxxdev.hrm.core.HRMModel
import com.github.alexxxdev.hrm.core.IHRM
import com.github.alexxxdev.hrm.core.IO
import com.github.alexxxdev.hrm.core.OS
import com.github.alexxxdev.hrm.core.OSType

class HRM : IHRM {
    val io = IO()
    var hrmModel = HRMModel()

    init {
        hrmModel = hrmModel.copy(os = OS(name = System.getProperty("os.name"), type = OSType.Windows))
        val cpuModel = getCPUModel()
        val gpuModel = getGPUModel()
        hrmModel = hrmModel.copy(cpu = CPU(name = cpuModel), gpu = GPU(name = gpuModel))
    }

    override fun getOSType(): OSType {
        return OSType.Windows
    }

    override fun getCPUModel(): String {
        return io.getShellOutput("wmic cpu get name")
            .replace("\r", "")
            .replace("\n", "")
            .replaceFirst("Name", "")
            .trim()
    }

    override fun getGPUModel(): String {
        return io.getShellOutput("wmic path win32_VideoController get name")
            .replace("\r", "")
            .replace("\n", "")
            .replace("Name", "")
            .trim()
    }

    override fun getData(): List<Result<HRMModel>> {
        val list = getValuesFromWMI()
        return if (list.isNullOrEmpty()) {
            listOf(Result.failure(java.lang.Exception()))
        } else {
            var fan = 0
            list.forEach {
                if (it[0] == "GPU Fan") {
                    fan = it[2].toInt()
                }
            }
            listOf(Result.success(hrmModel.copy(cpu = CPU(fan = fan))))
        }
    }

    @Throws(Exception::class)
    private fun getValuesFromWMI(): List<List<String>>? {
        return io.getShellOutput("powershell.exe get-wmiobject -namespace root\\OpenHardwareMonitor -query 'SELECT Value,Name,SensorType FROM Sensor'")
            .split("PSComputerName")
            .map {
                it.split("\r\n")
                    .filter {
                        it.startsWith("Name") ||
                                it.startsWith("SensorType") ||
                                it.startsWith("Value")
                    }
                    .map {
                        it.replaceBefore(":", "")
                            .replace(":", "")
                            .trim()
                    }
            }
            .filter { it.isNotEmpty() }
    }
}
