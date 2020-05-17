package com.github.alexxxdev.hrm.server

import com.github.alexxxdev.hrm.core.HRMModel
import com.github.alexxxdev.hrm.core.getVersion
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.cio.write
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.net.InetSocketAddress

var hrmModel = HRMModel()
val hrm = HRM()
val json = Json(JsonConfiguration.Stable)
val config = Config("config")

fun main(args: Array<String>) {
    if (!config.readConfig()) return

    if (getData(config.params)) return

    val version = hrmModel.getVersion()
    println(version)

    println(json.stringify(HRMModel.serializer(), hrmModel))

    CoroutineScope(Dispatchers.Default).launch {
        repeat(30) {
            if (getData(config.params)) return@launch
            println(json.stringify(HRMModel.serializer(), hrmModel))
            delay(config.refreshDataInterval)
        }
    }

    runBlocking {
        val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress(config.ip, config.port))
        println("Started server ...")
        delay(1000)
        println("Ready: ${server.localAddress}")
        while (true) {
            val socket = server.accept()
            var versionCompatibility = false
            launch {
                println("Socket accepted: ${socket.remoteAddress}")

                val input = socket.openReadChannel()
                val output = socket.openWriteChannel(autoFlush = true)

                try {
                    while (true) {
                        val line = input.readUTF8Line()
                        println("${socket.remoteAddress}: $line")
                        if (line != null) {
                            when {
                                line.startsWith("version:") -> {
                                    val clientVersion = line.split(":").last()
                                    versionCompatibility = clientVersion == version
                                    if(versionCompatibility) {
                                        output.write("The client compatible\r\n")
                                    } else {
                                        output.write("The client is not compatible\r\n")
                                    }
                                }
                                line == "get" -> {
                                    if (versionCompatibility) {
                                        output.write("${json.stringify(HRMModel.serializer(), hrmModel)}\r\n")
                                    } else {
                                        output.write("The client is not compatible\r\n")
                                        socket.close()
                                        return@launch
                                    }
                                }
                            }
                        } else {
                            socket.close()
                            return@launch
                        }
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    socket.close()
                }
            }
        }
    }
}

private fun getData(params: Map<String, String>): Boolean {
    val data = hrm.getData(params).first()
    if (data.isFailure) {
        println("Open Hardware Monitor not Running!")
        println("Please run Open Hardware Monitor with Admin Rights")
        return true
    }
    hrmModel = data.getOrThrow()
    return false
}
