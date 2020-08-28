package com.github.alexxxdev.hrm.server

import com.github.alexxxdev.hrm.core.IO
import kotlinx.serialization.json.Json
import java.io.File
import java.net.NetworkInterface

val jsonConfig = Json { prettyPrint = true }

class Config(val name: String) {
    private val configFile = File(name)
    private val io = IO()
    private var isEmpty = true
    private var isCorrect = false
    private var model: ConfigModel = ConfigModel()

    val absolutePath: String = configFile.absolutePath
    val ip: String
        get() = model.ip
    val port: Int
        get() = model.port
    val refreshDataInterval: Long
        get() = model.refreshDataInterval
    val params: Map<String, String>
        get() = model.params

    fun readConfig(): Boolean {
        return if (exists()) {
            read()
            if (isEmpty) {
                prefillConfig()
                println("Need to fill out the config ${config.absolutePath}")
                false
            } else if (!isCorrect) {
                println("Need to fill out the config ${config.absolutePath}")
                false
            } else {
                true
            }
        } else {
            if (createNewFile()) {
                prefillConfig()
                println("Need to fill out the config ${config.absolutePath}")
            } else {
                println("It is necessary to create and fill in the config ${config.absolutePath}")
            }
            false
        }
    }

    private fun exists(): Boolean = configFile.exists()

    private fun createNewFile(): Boolean = configFile.createNewFile()

    private fun read() {
        try {
            val raw = configFile.readText()
            model = jsonConfig.decodeFromString(ConfigModel.serializer(), raw)
            println(model)
            isEmpty = false
            isCorrect = check()
        } catch (e: Exception) {
            println(e)
            isEmpty = true
        }
    }

    private fun check(): Boolean {

        return true
    }

    private fun prefillConfig() {
        val model = model.copy(
            ip = "Maybe: ${getIps()}",
            params = mapOf(
                "cpu.load" to "CPU Total",
                "cpu.temperature" to "CPU Package",
                "cpu.fan" to "Fan #1",
                "gpu.load" to "GPU Core",
                "gpu.temperature" to "GPU Core",
                "gpu.fan" to "GPU Fan",
                "gpu.memory" to "GPU Memory",
                "memory.total" to "Memory",
                "memory.used" to "Used Memory"
            )
        )
        val config = jsonConfig.encodeToString(ConfigModel.serializer(), model)
        configFile.writeText(config)
    }

    private fun getIps(): List<String> {
        return NetworkInterface.getNetworkInterfaces()
            .asSequence()
            .toList()
            .filter { it.isUp }
            .map { it.interfaceAddresses }
            .flatMap {
                it.filter { it.address.isSiteLocalAddress }
                    .map {
                        println(it.address.hostAddress)
                        it.address.hostAddress
                    }
            }
            .toList()
    }
}
