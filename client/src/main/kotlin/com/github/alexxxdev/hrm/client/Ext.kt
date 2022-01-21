package com.github.alexxxdev.hrm.client

import com.github.alexxxdev.hrm.client.model.Weather

val Weather.condition: String
    get() = when (this.fact.condition) {
        "clear" -> "ясно"
        "partly-cloudy" -> "малооблачно"
        "cloudy" -> "облачно с прояснениями"
        "overcast" -> "пасмурно"
        "drizzle" -> "морось"
        "light-rain" -> "небольшой дождь"
        "rain" -> "дождь"
        "moderate-rain" -> "умеренно сильный дождь"
        "heavy-rain" -> "сильный дождь"
        "continuous-heavy-rain" -> "длительный сильный дождь"
        "showers" -> "ливень"
        "wet-snow" -> "дождь со снегом"
        "light-snow" -> "небольшой снег"
        "snow" -> "снег"
        "snow-showers" -> "снегопад"
        "hail" -> "град"
        "thunderstorm" -> "гроза"
        "thunderstorm-with-rain" -> "дождь с грозой"
        "thunderstorm-with-hail" -> "гроза с градом"
        else -> ""
    }
val Weather.windDir: String
    get() = when (this.fact.wind_dir) {
        "n" -> "\uD83E\uDC79 с"
        "ne" -> "\uD83E\uDC7D св"
        "e" -> "\uD83E\uDC7A в"
        "se" -> "\uD83E\uDC7E юв"
        "s" -> "\uD83E\uDC7B ю"
        "sw" -> "\uD83E\uDC7F юз"
        "w" -> "\uD83E\uDC78 з"
        "nw" -> "\uD83E\uDC7C сз"
        "с" -> "Штиль"
        else -> ""
    }

val Weather.iconUrl: String
    get() = "https://yastatic.net/weather/i/icons/funky/dark/${this.fact.icon}.svg"
