package com.github.alexxxdev.hrm.server

import com.github.alexxxdev.hrm.core.HRMModel
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
import java.net.InetSocketAddress

var hrmModel = HRMModel()
val hrm = HRM()

fun main(args: Array<String>) {
    if (getData()) return
    println(hrmModel)

    CoroutineScope(Dispatchers.Default).launch {
        repeat(1) {
            if (getData()) return@launch
            delay(1000)
        }
    }

    runBlocking {
        val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress("127.0.0.1", 2323))
        println("Started server ...")
        delay(1000)
        println("Ready: ${server.localAddress}")
        while (true) {
            val socket = server.accept()

            launch {
                println("Socket accepted: ${socket.remoteAddress}")

                val input = socket.openReadChannel()
                val output = socket.openWriteChannel(autoFlush = true)

                try {
                    while (true) {
                        val line = input.readUTF8Line()
                        println("${socket.remoteAddress}: $line")
                        if (line != null) {
                            output.write("$hrmModel\r\n")
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

private fun getData(): Boolean {
    val data = hrm.getData().first()
    if (data.isFailure) {
        println("Open Hardware Monitor not Running!")
        println("Please run Open Hardware Monitor with Admin Rights")
        return true
    }
    hrmModel = data.getOrThrow()
    return false
}
