package com.project.horizon.data.remote.weather.models

import com.project.horizon.R

// --- Weather Code Groups ---

/**
 * Weather codes representing mainly clear, partly cloudy, and overcast conditions.
 */
private val cloudyWeatherCodes = setOf(1, 2, 3)

/**
 * Weather codes for drizzle (light to dense), freezing drizzle, and rain showers (slight to violent).
 */
private val rainyWeatherCodes = setOf(51, 53, 55, 56, 57, 80, 81, 82)

/**
 * Weather codes for rain, freezing rain, and thunderstorms (with or without hail).
 */
private val thunderstormsWeatherCodes = setOf(61, 63, 65, 66, 67, 95, 96, 99)

/**
 * Weather codes for snow, snow grains, and snow showers.
 */
private val snowWeatherCodes = setOf(71, 73, 75, 77, 85, 86)

/**
 * Weather codes representing fog and depositing rime fog.
 */
private val fogWeatherCodes = setOf(45, 48)

/**
 * Returns a drawable resource ID for a background image that visually represents the weather condition
 * based on the [weatherCode] and time of day indicated by [isDay].
 *
 * @param weatherCode The weather code representing current conditions (e.g., clear, rain, snow).
 * @param isDay `true` if it is daytime, `false` for nighttime.
 * @return Resource ID of a drawable image matching the weather condition and time.
 * @throws IllegalArgumentException If the weather code is not recognized.
 */
fun getWeatherImageForCode(weatherCode: Int, isDay: Boolean): Int {
    return if (isDay) {
        when (weatherCode) {
            0 -> R.drawable.img_day_clear
            in cloudyWeatherCodes -> R.drawable.img_day_cloudy
            in rainyWeatherCodes -> R.drawable.img_day_rain
            in thunderstormsWeatherCodes -> R.drawable.img_day_rain
            in snowWeatherCodes -> R.drawable.img_day_snow
            in fogWeatherCodes -> R.drawable.img_day_fog
            else -> throw IllegalArgumentException("Unknown weatherCode $weatherCode")
        }
    } else {
        when (weatherCode) {
            0 -> R.drawable.img_night_clear
            in cloudyWeatherCodes -> R.drawable.img_night_cloudy
            in rainyWeatherCodes -> R.drawable.img_night_rain
            in thunderstormsWeatherCodes -> R.drawable.img_night_rain
            in snowWeatherCodes -> R.drawable.img_night_snow
            in fogWeatherCodes -> R.drawable.img_night_fog
            else -> throw IllegalArgumentException("Unknown weatherCode $weatherCode")
        }
    }
}

/**
 * Returns a vector icon resource ID representing the current weather condition indicated by the [weatherCode].
 *
 * Icons are optimized for compact display (e.g., in UI components like cards or lists).
 *
 * @param weatherCode The numeric weather code.
 * @param isDay `true` if it's daytime; otherwise, `false`.
 * @return The resource ID of the corresponding weather icon.
 * @throws IllegalArgumentException If the weather code is not handled.
 */
fun getWeatherIconResForCode(weatherCode: Int, isDay: Boolean): Int {
    return if (isDay) {
        when (weatherCode) {
            0 -> R.drawable.ic_day_clear
            in cloudyWeatherCodes -> R.drawable.ic_day_few_clouds
            in rainyWeatherCodes -> R.drawable.ic_day_rain
            in thunderstormsWeatherCodes -> R.drawable.ic_day_thunderstorm
            in snowWeatherCodes -> R.drawable.ic_day_snow
            in fogWeatherCodes -> R.drawable.ic_mist
            else -> throw IllegalArgumentException("Unknown weatherCode $weatherCode")
        }
    } else {
        when (weatherCode) {
            0 -> R.drawable.ic_night_clear
            in cloudyWeatherCodes -> R.drawable.ic_night_few_clouds
            in rainyWeatherCodes -> R.drawable.ic_night_rain
            in thunderstormsWeatherCodes -> R.drawable.ic_night_thunderstorm
            in snowWeatherCodes -> R.drawable.ic_night_snow
            in fogWeatherCodes -> R.drawable.ic_mist
            else -> throw IllegalArgumentException("Unknown weatherCode $weatherCode")
        }
    }
}
