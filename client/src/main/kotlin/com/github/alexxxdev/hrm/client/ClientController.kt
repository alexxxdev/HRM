package com.github.alexxxdev.hrm.client

import com.github.alexxxdev.hrm.client.view.MainView
import com.github.alexxxdev.hrm.core.HRMModel
import com.github.alexxxdev.hrm.core.getVersion
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.SocketBuilder
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.cio.write
import io.ktor.utils.io.readUTF8Line
import javafx.application.Platform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornadofx.Controller
import java.net.ConnectException
import java.net.InetSocketAddress

class ClientController : Controller() {
    val view: MainView by inject()

    fun init() {
        view.visibilityConnectPane(true)
    }

    fun connect(text: String?) {
        text?.let {
            val ip = it
            println(text)
            view.visibilityConnectPane(false)

            CoroutineScope(Dispatchers.Default).launch {
                var socket: Socket?=null
                try {
                    socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress(ip, 2323))
                } catch (e: ConnectException) {
                    e.printStackTrace()
                    Platform.runLater {
                        view.visibilityConnectPane(true)
                        view.showMessage(e.localizedMessage)
                    }
                }
                socket?.let {
                    val input = socket.openReadChannel()
                    val output = socket.openWriteChannel(autoFlush = true)

                    output.write("version:${HRMModel().getVersion()}\r\n")
                    val response = input.readUTF8Line()
                    println("Server said: '$response'")

                    output.write("get\r\n")
                    val response2 = input.readUTF8Line()
                    println("Server said: '$response2'")
                }
            }
        }
    }
}