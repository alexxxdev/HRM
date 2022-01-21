package com.github.alexxxdev.hrm.server

import com.github.alexxxdev.hrm.core.Config
import java.net.NetworkInterface

class ServerConfig(name: String) : Config<ConfigModel>(name) {

    override var model: ConfigModel = ConfigModel()

    val ip: String
        get() = model.ip
    val port: Int
        get() = model.port
    val refreshDataInterval: Long
        get() = model.refreshDataInterval
    val params: Map<String, String>
        get() = model.params

    override fun prefillConfig() = model.copy(
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

    override fun serializer() = ConfigModel.serializer()

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
