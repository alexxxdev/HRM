package com.github.alexxxdev.hrm.client.app

import com.github.alexxxdev.hrm.client.ClientController
import com.github.alexxxdev.hrm.client.view.MainView
import javafx.application.Platform
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.App
import java.awt.AWTException
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.event.ActionListener

const val TITLE = "Title App Name"
const val ICON = "icon.png"
const val WIDTH = 800.0
const val HEIGHT = 480.0

class MyApp : App(MainView::class, Styles::class) {
    val controller: ClientController by inject()
    var trayIcon: TrayIcon? = null

    init {
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            println("DefaultUncaughtExceptionHandler... " + e.localizedMessage)
        }
    }

    override fun start(stage: Stage) {
        controller.init()
        Platform.setImplicitExit(false)
        with(stage) {
            // initStyle(StageStyle.TRANSPARENT);
            isResizable = false
            // isFullScreen = true
            // fullScreenExitHint="ds"
            minWidth = WIDTH
            minHeight = HEIGHT
            icons.add(Image("/$ICON"))
        }

        super.start(stage)
        if (SystemTray.isSupported()) {
            val tray = SystemTray.getSystemTray()
            val image: java.awt.Image = Toolkit.getDefaultToolkit().getImage(javaClass.classLoader.getResource(ICON))
            val exitListener = ActionListener {
                println("Exiting...")
                tray.remove(trayIcon)
                Platform.exit()
            }
            val popup = PopupMenu()
            val defaultItem = MenuItem("Show")
            defaultItem.addActionListener(ActionListener {
                Platform.runLater { stage.show() }
            })
            val defaultItem1 = MenuItem("Exit")
            defaultItem1.addActionListener(exitListener)
            popup.add(defaultItem)
            popup.addSeparator()
            popup.add(defaultItem1)

            trayIcon = TrayIcon(image, "sa", popup)
            val actionListener = ActionListener {
                trayIcon?.displayMessage(
                    "Action Event",
                    "An Action Event Has Been Performed!",
                    TrayIcon.MessageType.INFO
                )
            }
            trayIcon?.isImageAutoSize = true
            /*trayIcon?.addActionListener(actionListener)
            trayIcon?.addMouseListener(object : MouseAdapter() {
                override fun mouseReleased(e: MouseEvent) {
                    Platform.runLater { stage.show() }
                }
            })*/
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
