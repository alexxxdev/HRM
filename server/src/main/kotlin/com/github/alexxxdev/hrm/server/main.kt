package com.github.alexxxdev.hrm.server

import com.github.alexxxdev.hrm.core.HRMModel
import com.github.alexxxdev.hrm.core.getVersion
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.cio.write
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.awt.AWTException
import java.awt.Image
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.event.ActionListener
import java.net.InetSocketAddress
import kotlin.system.exitProcess

const val TITLE = "HRM Server"
const val ICON = "icon.png"

var hrmModel = HRMModel()
val hrm = HRM()
val json = Json
val config = ServerConfig("config")
var trayIcon: TrayIcon? = null
var job: Job? = null

fun main() {
    if (!config.readConfig()) return

    if (!getData(config.params)) return

    val version = hrmModel.getVersion()
    println(version)

    println(json.encodeToString(HRMModel.serializer(), hrmModel))

    job = createJob()

    addSystemTray()

    runServer(version)
}

@OptIn(KtorExperimentalAPI::class)
private fun runServer(version: String) {
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
                                    if (versionCompatibility) {
                                        output.write("success\r\n")
                                    } else {
                                        output.write("fail\r\n")
                                    }
                                }
                                line == "get" -> {
                                    if (versionCompatibility) {
                                        output.write("${json.encodeToString(HRMModel.serializer(), hrmModel)}\r\n")
                                    } else {
                                        output.write("fail\r\n")
                                        withContext(Dispatchers.IO) {
                                            socket.close()
                                        }
                                        return@launch
                                    }
                                }
                            }
                        } else {
                            withContext(Dispatchers.IO) {
                                socket.close()
                            }
                            return@launch
                        }
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    withContext(Dispatchers.IO) {
                        socket.close()
                    }
                }
            }
        }
    }
}

private fun addSystemTray() {
    if (SystemTray.isSupported()) {
        val tray = SystemTray.getSystemTray()
        val image: Image =
            Toolkit.getDefaultToolkit().getImage(Toolkit::javaClass.javaClass.classLoader.getResource(ICON))
        val exitListener = ActionListener {
            println("Exiting...")
            tray.remove(trayIcon)
            exitProcess(0)
        }
        val popup = PopupMenu()
        val defaultItem = MenuItem("Restart")
        defaultItem.addActionListener {
            job?.cancel()
            job = createJob()
        }
        val defaultItem1 = MenuItem("Exit")
        defaultItem1.addActionListener(exitListener)
        popup.add(defaultItem)
        popup.addSeparator()
        popup.add(defaultItem1)

        trayIcon = TrayIcon(image, TITLE, popup)
        trayIcon?.isImageAutoSize = true
        try {
            tray.add(trayIcon)
        } catch (e: AWTException) {
            System.err.println("TrayIcon could not be added.")
        }
    }
}

private fun createJob(): Job {
    return CoroutineScope(Dispatchers.Default).launch {
        while (true) {
            if (!getData(config.params)) return@launch
            println(json.encodeToString(HRMModel.serializer(), hrmModel))
            delay(config.refreshDataInterval)
        }
    }
}

private fun getData(params: Map<String, String>): Boolean {
    val data = hrm.getData(params).first()
    if (data.isFailure) return false
    hrmModel = data.getOrThrow()
    return true
}
