package com.github.alexxxdev.hrm.client.view

import com.github.alexxxdev.hrm.client.ClientController
import com.github.alexxxdev.hrm.client.ClockX
import com.github.alexxxdev.hrm.client.MyTileSparklineSkin
import com.github.alexxxdev.hrm.client.app.HEIGHT
import com.github.alexxxdev.hrm.client.app.Styles
import com.github.alexxxdev.hrm.client.app.TITLE
import com.github.alexxxdev.hrm.client.app.WIDTH
import com.github.alexxxdev.hrm.core.HRMModel
import eu.hansolo.medusa.Clock
import eu.hansolo.medusa.Gauge
import eu.hansolo.medusa.Section
import io.ktor.util.KtorExperimentalAPI
import javafx.geometry.Pos
import javafx.scene.CacheHint
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import tornadofx.View
import tornadofx.action
import tornadofx.addClass
import tornadofx.attachTo
import tornadofx.button
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.paddingLeft
import tornadofx.paddingRight
import tornadofx.pane
import tornadofx.textfield
import tornadofx.vbox
import java.util.Locale

class MainView : View(TITLE) {
    private var tileSize = HEIGHT / 3
    private lateinit var connectPane: Pane
    private lateinit var connectMessage: Label

    val controller: ClientController by inject()
    lateinit var CPUName: Label
    lateinit var GPUName: Label
    lateinit var CPULoadGauge: Gauge
    lateinit var GPULoadGauge: Gauge
    lateinit var CPUMemoryGauge: Gauge
    lateinit var GPUMemoryGauge: Gauge
    lateinit var CPUTempGauge: Gauge
    lateinit var GPUTempGauge: Gauge
    lateinit var CPUFanGauge: Gauge
    lateinit var GPUFanGauge: Gauge

    fun visibilityConnectPane(value: Boolean) {
        connectPane.isVisible = value
        connectMessage.isVisible = false
    }

    fun showMessage(localizedMessage: String?) {
        connectMessage.text = localizedMessage
        connectMessage.isVisible = true
    }

    fun showModel(hrmModel: HRMModel) {
        CPUName.text = hrmModel.cpu.name
        GPUName.text = hrmModel.gpu.name

        CPULoadGauge.value = hrmModel.cpu.load.toDouble()
        GPULoadGauge.value = hrmModel.gpu.load.toDouble()

        CPUMemoryGauge.maxValue = hrmModel.memory.total.toDouble()
        CPUMemoryGauge.value = hrmModel.memory.used.toDouble()

        GPUMemoryGauge.maxValue = 100.0
        GPUMemoryGauge.value = hrmModel.gpu.usedMemory.toDouble()

        CPUTempGauge.value = hrmModel.cpu.temperature.toDouble()
        GPUTempGauge.value = hrmModel.gpu.temperature.toDouble()

        CPUFanGauge.value = (100.0 / 1510.0) * hrmModel.cpu.fan.toDouble()
        GPUFanGauge.value = hrmModel.gpu.fan.toDouble()
    }

    @KtorExperimentalAPI
    override val root = pane {
        setPrefSize(WIDTH, HEIGHT)

        CPUName = label() {
            paddingAll = 10.0
            prefWidth = WIDTH / 2
            alignment = Pos.CENTER_LEFT
            addClass(Styles.header)
        }

        GPUName = label() {
            paddingAll = 10.0
            prefWidth = WIDTH / 2
            layoutX = WIDTH - WIDTH / 2
            alignment = Pos.CENTER_RIGHT
            addClass(Styles.header)
        }

        CPULoadGauge = Gauge().attachTo(this) {
            setPrefSize(tileSize * 1.6, tileSize)
            skinType = Gauge.SkinType.TILE_SPARK_LINE
            averagingPeriod = 30
            layoutY = 10.0
            minValue = 0.0
            maxValue = 100.0
            unit = "%"
            isAutoScale = false
            isSmoothing = true
            isAnimated = false
            backgroundPaint = Color.TRANSPARENT
            valueColor = Clock.BRIGHT_COLOR
            unitColor = Clock.BRIGHT_COLOR
            isCache = true
            cacheHint = CacheHint.SPEED
            paddingLeft = 10
        }

        CPUMemoryGauge = Gauge().attachTo(this) {
            setPrefSize(tileSize, tileSize)
            skinType = Gauge.SkinType.SIMPLE_SECTION
            layoutY = 10.0 + tileSize * 2
            title = "Memory"
            unit = "Gb"
            isAutoScale = false
            isSmoothing = true
            backgroundPaint = Color.TRANSPARENT
            valueColor = Clock.BRIGHT_COLOR
            unitColor = Clock.BRIGHT_COLOR
            isCache = true
            cacheHint = CacheHint.SPEED
            paddingLeft = 10
        }

        CPUTempGauge = Gauge().attachTo(this) {
            setPrefSize(tileSize, tileSize)
            skinType = Gauge.SkinType.SIMPLE_SECTION
            layoutY = 10.0 + tileSize
            title = "Temp"
            unit = "°C"
            isAutoScale = false
            isSmoothing = true
            backgroundPaint = Color.TRANSPARENT
            valueColor = Clock.BRIGHT_COLOR
            unitColor = Clock.BRIGHT_COLOR
            isCache = true
            cacheHint = CacheHint.SPEED
            minValue = 25.0
            maxValue = 120.0
            setSections(
                Section(25.0, 35.0, Color.BLUE),
                Section(35.01, 65.0, Color.GREEN),
                Section(65.01, 85.0, Color.ORANGE),
                Section(85.01, 120.0, Color.RED)
            )
            paddingLeft = 10
        }

        CPUFanGauge = Gauge().attachTo(this) {
            setPrefSize(tileSize * 0.8, tileSize * 0.8)
            skinType = Gauge.SkinType.SIMPLE_SECTION // BAR
            layoutY = tileSize * 2 - 50
            layoutX = tileSize - 30
            title = "Fan"
            unit = "%"
            isAutoScale = false
            isSmoothing = true
            backgroundPaint = Color.TRANSPARENT
            barColor = Color.rgb(183, 183, 183)
            valueColor = Clock.BRIGHT_COLOR
            unitColor = Clock.BRIGHT_COLOR
            isCache = true
            cacheHint = CacheHint.SPEED
            paddingLeft = 10
            maxValue = 100.0
        }

        GPULoadGauge = Gauge().attachTo(this) {
            setPrefSize(tileSize * 1.6, tileSize)
            skinType = Gauge.SkinType.TILE_SPARK_LINE
            averagingPeriod = 25
            angleRange = 170.0
            layoutY = 10.0
            layoutX = WIDTH - tileSize * 1.6
            maxValue = 100.0
            unit = "%"
            isAutoScale = false
            isSmoothing = true
            backgroundPaint = Color.TRANSPARENT
            valueColor = Clock.BRIGHT_COLOR
            unitColor = Clock.BRIGHT_COLOR
            isCache = true
            cacheHint = CacheHint.SPEED
            paddingRight = 10
        }

        GPUMemoryGauge = Gauge().attachTo(this) {
            setPrefSize(tileSize, tileSize)
            skinType = Gauge.SkinType.SIMPLE_SECTION
            layoutY = 10.0 + tileSize * 2
            layoutX = WIDTH - tileSize
            title = "Memory"
            unit = "%"
            isAutoScale = false
            isSmoothing = true
            backgroundPaint = Color.TRANSPARENT
            valueColor = Clock.BRIGHT_COLOR
            unitColor = Clock.BRIGHT_COLOR
            isCache = true
            cacheHint = CacheHint.SPEED
            paddingRight = 10
        }

        GPUTempGauge = Gauge().attachTo(this) {
            setPrefSize(tileSize, tileSize)
            skinType = Gauge.SkinType.SIMPLE_SECTION
            layoutY = 10.0 + tileSize
            layoutX = WIDTH - tileSize
            title = "Temp"
            unit = "°C"
            isAutoScale = false
            isSmoothing = true
            backgroundPaint = Color.TRANSPARENT
            valueColor = Clock.BRIGHT_COLOR
            unitColor = Clock.BRIGHT_COLOR
            isCache = true
            cacheHint = CacheHint.SPEED
            setSections(
                Section(0.0, 35.0, Color.BLUE),
                Section(35.01, 65.0, Color.GREEN),
                Section(65.01, 75.0, Color.ORANGE),
                Section(75.01, 100.0, Color.RED)
            )
            paddingRight = 10
        }

        GPUFanGauge = Gauge().attachTo(this) {
            setPrefSize(tileSize * 0.8, tileSize * 0.8)
            skinType = Gauge.SkinType.SIMPLE_SECTION // BAR
            layoutY = tileSize * 2 - 50
            layoutX = WIDTH - tileSize * 2 + 52
            title = "Fan"
            unit = "%"
            isAutoScale = false
            isSmoothing = true
            backgroundPaint = Color.TRANSPARENT
            barColor = Color.rgb(183, 183, 183)
            valueColor = Clock.BRIGHT_COLOR
            unitColor = Clock.BRIGHT_COLOR
            isCache = true
            cacheHint = CacheHint.SPEED
            paddingLeft = 10
            maxValue = 100.0
        }

        ClockX().attachTo(this) {
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

        connectPane = vbox {
            isVisible = false
            addClass(Styles.clock)
            spacing = 10.0
            paddingAll = 10.0
            alignment = Pos.CENTER
            setPrefSize(tileSize * 2, tileSize)
            CPULoadGauge.skin = MyTileSparklineSkin(CPULoadGauge)
            GPULoadGauge.skin = MyTileSparklineSkin(GPULoadGauge)
            label("Connect to server(ip):") {
                addClass(Styles.heading)
                prefWidth(tileSize)
            }
            val ip = textfield("192.168.0.11")
            button("Connect") {
                action { controller.connect(ip.text) }
            }
            connectMessage = label {
                addClass(Styles.heading)
                isVisible = false
            }
        }
    }
}
