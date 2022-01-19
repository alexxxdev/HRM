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
    private val io = IO()
    private var hrmModel = HRMModel()

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

    override fun getData(params: Map<String, String>): List<Result<HRMModel>> {
        val list = getValuesFromWMI()
        return if (list.isNullOrEmpty()) {
            println("Open Hardware Monitor not Running!")
            println("Please run Open Hardware Monitor with Admin Rights")
            listOf(Result.failure(java.lang.Exception()))
        } else {
            var CPUload: Float = 0f
            var CPUtemperature: Float = 0f
            var CPUfan: Float = 0f
            var CPUfanMax: Float = 0f
            var GPUload: Float = 0f
            var GPUtemperature: Float = 0f
            var GPUfan: Float = 0f
            var GPUUsedMemory: Float = 0f
            var MemoryUsed: Float = 0f
            var MemoryTotal: Float = 0f
            list.forEach {
                if (it[1] == params.get("cpu.load") && it[2] == "Load") {
                    CPUload = it[3].replace(',', '.').toFloat()
                }
                if (it[1] == params.get("cpu.temperature") && it[2] == "Temperature") {
                    CPUtemperature = it[3].replace(',', '.').toFloat()
                }
                if (it[1] == params.get("cpu.fan") && it[2] == "Fan") {
                    CPUfan = it[3].replace(',', '.').toFloat()
                    CPUfanMax = it[0].replace(',', '.').toFloat()
                }
                if (it[1] == params.get("gpu.load") && it[2] == "Load") {
                    GPUload = it[3].replace(',', '.').toFloat()
                }
                if (it[1] == params.get("gpu.temperature") && it[2] == "Temperature") {
                    GPUtemperature = it[3].replace(',', '.').toFloat()
                }
                if (it[1] == params.get("gpu.fan") && it[2] == "Control") {
                    GPUfan = it[3].replace(',', '.').toFloat()
                }
                if (it[1] == params.get("memory.used") && it[2] == "Data") {
                    MemoryUsed = it[3].replace(',', '.').toFloat()
                }
                if (it[1] == params.get("memory.total") && it[2] == "Load") {
                    MemoryTotal = it[3].replace(',', '.').toFloat()
                }
                if (it[1] == params.get("gpu.memory") && it[2] == "Load") {
                    GPUUsedMemory = it[3].replace(',', '.').toFloat()
                }
            }
            listOf(
                Result.success(
                    hrmModel.copy(
                        cpu = hrmModel.cpu.copy(
                            load = CPUload,
                            temperature = CPUtemperature,
                            fan = (CPUfan * (100.0 / CPUfanMax)).toFloat()
                        ),
                        gpu = hrmModel.gpu.copy(
                            load = GPUload,
                            temperature = GPUtemperature,
                            fan = GPUfan,
                            usedMemory = GPUUsedMemory
                        ),
                        memory = hrmModel.memory.copy(
                            used = MemoryUsed,
                            total = ((MemoryUsed * 100) / MemoryTotal).roundToInt().toFloat()
                        )
                    )
                )
            )
        }
    }

    @Throws(Exception::class)
    private fun getValuesFromWMI(): List<List<String>>? {
        return io.getShellOutput("powershell.exe get-wmiobject -namespace root\\OpenHardwareMonitor -query 'SELECT Max,Value,Name,SensorType FROM Sensor'")
            .split("PSComputerName")
            .map {
                it.split("\r\n")
                    .filter {
                        it.startsWith("Name") ||
                                it.startsWith("SensorType") ||
                                it.startsWith("Value") ||
                                it.startsWith("Max")
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
