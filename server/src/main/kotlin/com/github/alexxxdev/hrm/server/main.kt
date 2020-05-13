package com.github.alexxxdev.hrm.server

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.cio.write
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress

fun main(args: Array<String>) {
    println("Hello, World")
    runBlocking {
        val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress("127.0.0.1", 2323))
        println("Started echo telnet server at ${server.localAddress}")

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
                            output.write("$line+1\r\n")
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
