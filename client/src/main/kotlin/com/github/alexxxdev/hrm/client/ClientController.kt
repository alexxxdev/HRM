package com.github.alexxxdev.hrm.client

import com.github.alexxxdev.hrm.client.view.MainView
import com.github.alexxxdev.hrm.core.HRMModel
import com.github.alexxxdev.hrm.core.getVersion
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.KtorExperimentalAPI
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
import tornadofx.Controller
import tornadofx.DefaultErrorHandler
import java.net.InetSocketAddress
import java.net.SocketException
import kotlin.coroutines.CoroutineContext

class ClientController : Controller() {
    val view: MainView by inject()
    val json = Json
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("CoroutineExceptionHandler: " + throwable.localizedMessage)
        Platform.runLater {
            view.visibilityConnectPane(true)
            view.showMessage(throwable.localizedMessage)
        }
    }
    val coroutineContext: CoroutineContext = Dispatchers.Default + SupervisorJob() + coroutineExceptionHandler

    fun init() {
        view.visibilityConnectPane(true)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @KtorExperimentalAPI
    fun connect(text: String?) {
        text?.let {
            val ip = it
            println(text)
            view.visibilityConnectPane(false)

            CoroutineScope(coroutineContext).launch {
                val socket: Socket =
                    aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress(ip, 2323))

                val input = socket.openReadChannel()
                val output = socket.openWriteChannel(autoFlush = true)

                output.write("version:${HRMModel().getVersion()}\r\n")
                val response = input.readUTF8Line()
                // println("Server said: '$response'")
                if (response == "success") {
                    while (true) {
                        delay(1000)
                        output.write("get\r\n")
                        val response2 = input.readUTF8Line()
                        // println("Server said: '$response2'")
                        if (response2 == "fail" || response2 == null) {
                            socket.close()
                            return@launch
                        }
                        val hrmModel = json.decodeFromString(HRMModel.serializer(), response2)
                        Platform.runLater {
                            view.showModel(hrmModel)
                        }
                    }
                }
            }
        }
    }

    fun handleException(throwable: DefaultErrorHandler.ErrorEvent) {
        println("ErrorEvent.error: " + throwable.error)
        println("ErrorEvent.thread: " + throwable.thread)
        when (throwable.error) {
            is SocketException -> {
                throwable.consume()
                coroutineExceptionHandler.handleException(coroutineContext, throwable.error)
            }
        }
    }
}
