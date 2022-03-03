package com.github.alexxxdev.hrm.client.app

import com.github.alexxxdev.hrm.client.ClientConfig
import com.github.alexxxdev.hrm.client.ClientController
import com.github.alexxxdev.hrm.client.YandexWeatherController
import com.github.alexxxdev.hrm.client.view.MainView
import com.github.alexxxdev.hrm.core.Log
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory
import javafx.application.Platform
import javafx.scene.Cursor
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.App
import tornadofx.DefaultErrorHandler
import java.awt.AWTException
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.event.ActionListener

const val TITLE = "HRM Client"
const val ICON = "icon.png"
const val WIDTH = 800.0
const val HEIGHT = 480.0

val clientConfig = ClientConfig("config")

class MyApp : App(MainView::class, Styles::class) {
    val controller: ClientController by inject()
    val weatherController: YandexWeatherController by inject()
    var trayIcon: TrayIcon? = null

    init {
        DefaultErrorHandler.filter = { errorEvent ->
            controller.handleException(errorEvent)
        }
        SvgImageLoaderFactory.install()

        if (!clientConfig.readConfig()) Platform.exit()
        Log.debug = clientConfig.debug
    }

    override fun start(stage: Stage) {
        controller.init(clientConfig)
        weatherController.init(clientConfig.weather)
        Platform.setImplicitExit(false)
        with(stage) {
            if (clientConfig.fullscreen) {
                initStyle(StageStyle.TRANSPARENT)
            }
            isResizable = !clientConfig.fullscreen
            isFullScreen = clientConfig.fullscreen
            minWidth = WIDTH
            minHeight = HEIGHT
            icons.add(Image("/$ICON"))
        }

        super.start(stage)

        if (clientConfig.fullscreen) {
            stage.scene?.cursor = Cursor.NONE
        }

        if (SystemTray.isSupported()) {
            val tray = SystemTray.getSystemTray()
            val image: java.awt.Image = Toolkit.getDefaultToolkit().getImage(javaClass.classLoader.getResource(ICON))
            val exitListener = ActionListener {
                Log.d("Exiting...")
                tray.remove(trayIcon)
                Platform.exit()
            }
            val popup = PopupMenu()
            val defaultItem = MenuItem("Show")
            defaultItem.addActionListener {
                Platform.runLater { stage.show() }
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
        } else {
            /*try {
                dorkbox.systemTray.SystemTray.DEBUG = true
                //dorkbox.systemTray.SystemTray.FORCE_GTK2 = false
                //dorkbox.systemTray.SystemTray.PREFER_GTK3 = true
                dorkbox.systemTray.SystemTray.FORCE_TRAY_TYPE = TrayType.AWT
                val tray = dorkbox.systemTray.SystemTray.get()
                if(tray!=null){
                    /*tray.setTooltip("Mail Checker");
                    //tray.setImage(Toolkit.getDefaultToolkit().getImage(javaClass.classLoader.getResource("icon.png")));
                    tray.setStatus("No Mail");
                    val mainMenu = tray.getMenu()
                    val m = dorkbox.systemTray.MenuItem("Quit", ActionListener{
                        tray.shutdown();
                        Platform.exit()
                    })
                    mainMenu.add(m)*/
                }
            } catch (e: Exception) {
                e.printStackTrace();
            }*/
        }
        stage.setOnCloseRequest {
            /*if (SystemTray.isSupported()) {
                SystemTray.getSystemTray().remove(trayIcon)
                Platform.exit()
            }*/
        }
    }
}
