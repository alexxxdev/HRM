package com.github.alexxxdev.hrm.client

import com.github.alexxxdev.hrm.client.view.MainView
import com.github.alexxxdev.hrm.core.HRMModel
import com.github.alexxxdev.hrm.core.getVersion
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.cio.write
import io.ktor.utils.io.readUTF8Line
import javafx.application.Platform
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import tornadofx.Controller
import java.net.ConnectException
import java.net.InetSocketAddress
import kotlin.coroutines.CoroutineContext

class ClientController : Controller() {
    val view: MainView by inject()
    val json = Json(JsonConfiguration.Stable)
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("CoroutineExceptionHandler: " + throwable.localizedMessage)
    }
    val coroutineContext: CoroutineContext = Dispatchers.Default + SupervisorJob() + coroutineExceptionHandler

    fun init() {
        view.visibilityConnectPane(true)
    }

    fun connect(text: String?) {
        text?.let {
            val ip = it
            println(text)
            view.visibilityConnectPane(false)

            CoroutineScope(coroutineContext).launch {
                var socket: Socket? = null
                try {
                    socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress(ip, 2323))
                } catch (e: ConnectException) {
                    println("CoroutineExceptionHandler: " + e.localizedMessage)
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
                    if (response == "success") {
                        while (true) {
                            delay(1000)
                            output.write("get\r\n")
                            val response2 = input.readUTF8Line()
                            println("Server said: '$response2'")
                            if (response2 == "fail" || response2 == null) {
                                socket.close()
                                return@launch
                            }
                            val hrmModel = json.parse(HRMModel.serializer(), response2)
                            Platform.runLater {
                                view.showModel(hrmModel)
                            }
                        }
                    }
                }
            }
        }
    }

    fun handleException(throwable: Throwable?) {
        Platform.runLater {
            view.visibilityConnectPane(true)
            view.showMessage(throwable?.localizedMessage.orEmpty())
        }
    }
}
