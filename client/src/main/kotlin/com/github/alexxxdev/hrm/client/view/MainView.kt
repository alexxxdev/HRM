package com.github.alexxxdev.hrm.client.view

import com.github.alexxxdev.hrm.client.ClientController
import com.github.alexxxdev.hrm.client.ClockX
import com.github.alexxxdev.hrm.client.MyTileSparklineSkin
import com.github.alexxxdev.hrm.client.app.HEIGHT
import com.github.alexxxdev.hrm.client.app.Styles
import com.github.alexxxdev.hrm.client.app.TITLE
import com.github.alexxxdev.hrm.client.app.WIDTH
import com.github.alexxxdev.hrm.client.condition
import com.github.alexxxdev.hrm.client.iconUrl
import com.github.alexxxdev.hrm.client.model.Weather
import com.github.alexxxdev.hrm.client.windDir
import com.github.alexxxdev.hrm.core.HRMModel
import eu.hansolo.medusa.Clock
import eu.hansolo.medusa.Gauge
import eu.hansolo.medusa.Section
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.CacheHint
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BackgroundImage
import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BackgroundSize
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import tornadofx.View
import tornadofx.action
import tornadofx.addClass
import tornadofx.attachTo
import tornadofx.button
import tornadofx.clear
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.paddingLeft
import tornadofx.paddingRight
import tornadofx.pane
import tornadofx.textfield
import tornadofx.vbox
import java.util.Locale

@Suppress("VariableNaming", "MagicNumber")
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
    lateinit var weatherBox: Pane
    lateinit var weatherDescBox: Pane
    lateinit var textIP: TextField

    override val root = pane {
        setPrefSize(WIDTH, HEIGHT)

        background = Background(
            BackgroundImage(
                Image("/background.png", 480.0, 320.0, false, true),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize(WIDTH, HEIGHT, false, false, false, false)
            )
        )

        weatherBox = vbox {
            prefWidth = tileSize * 2
            layoutX = WIDTH / 2 - prefWidth / 2
            layoutY = HEIGHT / 2 - prefHeight / 2 - tileSize / 2 - 60
            alignment = Pos.CENTER
        }

        weatherDescBox = vbox {
            prefWidth = tileSize * 2
            prefHeight = 50.0
            layoutX = WIDTH / 2 - prefWidth / 2
            layoutY = HEIGHT - 78
            alignment = Pos.CENTER
        }

        button("Hide") {
            setPrefSize(100.0, 30.0)
            background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii(4.0), Insets.EMPTY))
            textFill = Color.rgb(58, 58, 58)
            layoutX = WIDTH / 2 - 50
            layoutY = HEIGHT - 34
            action {
                currentStage?.hide()
            }
        }

        CPUName = label() {
            paddingAll = 10.0
            prefWidth = WIDTH / 2
            layoutY = 16.0
            layoutX = 10.0
            alignment = Pos.CENTER_LEFT
            addClass(Styles.header)
        }

        GPUName = label() {
            paddingAll = 10.0
            prefWidth = WIDTH / 2
            layoutY = 16.0
            layoutX = WIDTH - WIDTH / 2 - 10
            alignment = Pos.CENTER_RIGHT
            addClass(Styles.header)
        }

        CPULoadGauge = Gauge().attachTo(this) {
            setPrefSize(tileSize * 1.5, tileSize)
            skinType = Gauge.SkinType.TILE_SPARK_LINE
            averagingPeriod = 30
            layoutY = 10.0
            layoutX = 10.0
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
            setPrefSize(tileSize * 0.9, tileSize * 0.9)
            skinType = Gauge.SkinType.SIMPLE_SECTION
            layoutY = 6.0 + tileSize * 2
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
            layoutY = tileSize
            layoutX = 17.0
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
            layoutY = tileSize * 2 - 40
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
            setPrefSize(tileSize * 1.5, tileSize)
            skinType = Gauge.SkinType.TILE_SPARK_LINE
            averagingPeriod = 25
            angleRange = 170.0
            layoutY = 10.0
            layoutX = WIDTH - tileSize * 1.5 - 10
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
            setPrefSize(tileSize * 0.9, tileSize * 0.9)
            skinType = Gauge.SkinType.SIMPLE_SECTION
            layoutY = 6.0 + tileSize * 2
            layoutX = WIDTH - tileSize * 0.9
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
            layoutY = tileSize
            layoutX = WIDTH - tileSize - 17
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
            layoutY = tileSize * 2 - 40
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
            textIP = textfield()
            button("Connect") {
                action { controller.connect(textIP.text) }
            }
            connectMessage = label {
                addClass(Styles.heading)
                isVisible = false
            }
        }
    }

    fun visibilityConnectPane(value: Boolean) {
        connectPane.isVisible = value
        connectMessage.isVisible = false
    }

    fun showMessage(localizedMessage: String?) {
        connectMessage.text = localizedMessage
        connectMessage.isVisible = true
    }

    fun setIP(serverIP: String) {
        textIP.text = serverIP
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

        CPUFanGauge.value = hrmModel.cpu.fan.toDouble()
        GPUFanGauge.value = hrmModel.gpu.fan.toDouble()
    }

    fun showWeather(weather: Weather) {
        weatherBox.clear()
        weatherDescBox.clear()

        weatherBox.add(
            hbox {
                prefWidth = tileSize * 2
                alignment = Pos.CENTER
                label("${weather.fact.temp}℃ ") {
                    addClass(Styles.weather)
                }
                imageview {
                    fitHeight = 24.0
                    fitWidth = 24.0
                    image = Image(weather.iconUrl, true)
                }
            }

        )
        weatherBox.add(
            label("Ощущается как ${weather.fact.feels_like}℃ ") {
                addClass(Styles.weather2)
            }
        )

        weatherDescBox.add(
            hbox {
                alignment = Pos.CENTER
                label("\uD83C\uDF22 ${weather.fact.humidity}%   \uD83C\uDF00 ${weather.fact.pressure_mm}") {
                    addClass(Styles.weather2)
                }
                label(" мм рт. ст.") {
                    addClass(Styles.weather3)
                }
            }
        )
        weatherDescBox.add(
            label("${weather.condition}      \uD83D\uDCA8 ${weather.fact.wind_speed} ${weather.windDir}") {
                addClass(Styles.weather2)
            }
        )
    }
}
