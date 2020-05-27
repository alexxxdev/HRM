package com.github.alexxxdev.hrm.client.view

import com.github.alexxxdev.hrm.client.ClientController
import com.github.alexxxdev.hrm.client.ClockX
import com.github.alexxxdev.hrm.client.app.HEIGHT
import com.github.alexxxdev.hrm.client.app.Styles
import com.github.alexxxdev.hrm.client.app.TITLE
import com.github.alexxxdev.hrm.client.app.WIDTH
import eu.hansolo.medusa.Clock
import eu.hansolo.medusa.Fonts
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import tornadofx.Stylesheet
import tornadofx.View
import tornadofx.action
import tornadofx.addClass
import tornadofx.attachTo
import tornadofx.bindStringProperty
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.hbox
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.pane
import tornadofx.textfield
import tornadofx.vbox
import java.util.Locale

class MainView : View(TITLE) {
    private var tileSize = HEIGHT / 3
    private lateinit var connectPane:Pane
    private lateinit var connectMessage:Label

    val controller: ClientController by inject()

    fun visibilityConnectPane(value:Boolean) {
        connectPane.isVisible = value
        connectMessage.isVisible = false
    }

    fun showMessage(localizedMessage: String?) {
        connectMessage.text = localizedMessage
        connectMessage.isVisible = true
    }

    override val root = pane {
        setPrefSize(WIDTH, HEIGHT)

        connectPane = vbox {
            isVisible = false
            addClass(Styles.clock)
            spacing = 10.0
            paddingAll = 10.0
            alignment = Pos.CENTER
            setPrefSize(tileSize*2, tileSize)

            label("Connect to server(ip):"){
                addClass(Styles.heading)
                prefWidth(tileSize)
            }
            val ip = textfield("127.0.0.1")
            button("Connect") {
                action { controller.connect(ip.text) }
            }
            connectMessage = label{
                addClass(Styles.heading)
                isVisible = false
            }
        }

        val CPUName = label("CPU: Intel(R) Core(TM) i7-8700 CPU @ 3.20GHz") {
            textFill = Clock.BRIGHT_COLOR
            font = Fonts.robotoRegular(15.0)
            paddingAll = 10.0
            prefWidth = WIDTH / 2
        }

        val GPUName = label("GPU: Lexa PRO [Radeon 540/540X/550/550X / RX 540X/550/550X]") {
            textFill = Clock.BRIGHT_COLOR
            font = Fonts.robotoRegular(15.0)
            paddingAll = 10.0
            prefWidth = WIDTH / 2
            layoutX = WIDTH - WIDTH / 2
        }

        val clock = ClockX().attachTo(this) {
            addClass(Styles.clock)
            setPrefSize(tileSize * 2, tileSize * 2)
            layoutX = WIDTH / 2 - prefWidth / 2
            layoutY = HEIGHT / 2 - prefHeight / 2

            isSecondsVisible = true
            isDateVisible = true
            isDayVisible = true
            hourColor = Color.WHITE
            minuteColor = Color.rgb(0, 191, 255)
            secondColor = Color.WHITE
            dateColor = Color.WHITE
            isRunning = true
            locale = Locale.getDefault()
        }
    }
}
