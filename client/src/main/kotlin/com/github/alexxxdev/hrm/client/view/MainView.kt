package com.github.alexxxdev.hrm.client.view

import com.github.alexxxdev.hrm.client.ClockX
import com.github.alexxxdev.hrm.client.app.HEIGHT
import com.github.alexxxdev.hrm.client.app.Styles
import com.github.alexxxdev.hrm.client.app.TITLE
import com.github.alexxxdev.hrm.client.app.WIDTH
import javafx.geometry.Pos
import javafx.scene.paint.Color
import tornadofx.*
import java.util.*

class MainView : View(TITLE) {
    private var tileSize = HEIGHT / 3

    override val root = pane {
        setPrefSize(WIDTH, HEIGHT)
        //alignment = Pos.CENTER
        hbox {
            addClass(Styles.clock)
            alignment = Pos.CENTER
            setPrefSize(tileSize, tileSize)
            label("...")
        }

        val clock = ClockX()
        clock.attachTo(this) {
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