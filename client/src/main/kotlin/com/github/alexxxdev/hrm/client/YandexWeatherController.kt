package com.github.alexxxdev.hrm.client

import com.github.alexxxdev.hrm.client.model.Weather
import com.github.alexxxdev.hrm.client.model.WeatherConfig
import com.github.alexxxdev.hrm.client.view.MainView
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import javafx.application.Platform
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import tornadofx.Controller
import java.time.LocalDateTime
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

class YandexWeatherController : Controller() {
    val view: MainView by inject()
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("WeatherController.CoroutineExceptionHandler: " + throwable.message)
    }
    val coroutineContext: CoroutineContext = Dispatchers.Default + SupervisorJob() + coroutineExceptionHandler

    @OptIn(ExperimentalTime::class)
    fun init(weatherConfig: WeatherConfig) {
        CoroutineScope(coroutineContext).launch {
            tickerFlow(1.toDuration(DurationUnit.HOURS))
                .map { LocalDateTime.now() }
                .onEach {
                    // println("WeatherController: " + it.toString())
                    HttpClient(Java) {
                        install(JsonFeature) {
                            serializer = KotlinxSerializer(
                                Json {
                                    ignoreUnknownKeys = true
                                }
                            )
                            expectSuccess = true
                        }
                        /*install(Logging) {
                            logger = Logger.DEFAULT
                            level = LogLevel.ALL
                        }*/
                    }.use { httpClient ->
                        val weather: Weather = httpClient.request<Weather> {
                            url {
                                protocol = URLProtocol.HTTPS
                                host = "api.weather.yandex.ru"
                                path("v2", "forecast")
                                parameters.append("lat", weatherConfig.lat.toString())
                                parameters.append("lon", weatherConfig.lon.toString())
                                parameters.append("lang", weatherConfig.lang)
                                headers.append("X-Yandex-API-Key", weatherConfig.token)
                            }
                            method = HttpMethod.Get
                        }
                        Platform.runLater {
                            view.showWeather(weather)
                        }
                    }
                }
                .launchIn(this)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
        delay(initialDelay)
        while (true) {
            emit(Unit)
            delay(period)
        }
    }
}
