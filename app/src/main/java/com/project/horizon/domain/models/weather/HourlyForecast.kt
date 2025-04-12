package com.project.horizon.domain.models.weather

import androidx.annotation.DrawableRes
import com.project.horizon.data.remote.weather.models.HourlyWeatherInfoResponse
import com.project.horizon.data.remote.weather.models.getWeatherIconResForCode
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.roundToInt

/**
 * Represents the weather forecast for a specific hour.
 *
 * @property dateTime The date and time of the forecast as a [LocalDateTime] instance.
 * @property weatherIconResId A drawable resource ID representing the weather condition.
 * @property temperature The temperature forecast for the hour, rounded to an [Int].
 */
data class HourlyForecast(
    val dateTime: LocalDateTime,
    @DrawableRes val weatherIconResId: Int,
    val temperature: Int
)

/**
 * Maps an [HourlyWeatherInfoResponse] from the data layer to a list of [HourlyForecast] domain models.
 *
 * @return A list of [HourlyForecast] items representing the hourly weather forecast.
 */
fun HourlyWeatherInfoResponse.toHourlyForecasts(): List<HourlyForecast> {
    val hourlyForecastList = mutableListOf<HourlyForecast>()

    for (i in hourlyForecast.timestamps.indices) {
        val epochSeconds = hourlyForecast.timestamps[i].toLong()
        val correspondingLocalTime = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(epochSeconds),
            ZoneId.systemDefault()
        )

        val weatherIconResId = getWeatherIconResForCode(
            weatherCode = hourlyForecast.weatherCodes[i],
            isDay = correspondingLocalTime.hour < 19 // Treat anything before 7 PM as day
        )

        val forecast = HourlyForecast(
            dateTime = correspondingLocalTime,
            weatherIconResId = weatherIconResId,
            temperature = hourlyForecast.temperatureForecasts[i].roundToInt()
        )

        hourlyForecastList.add(forecast)
    }

    return hourlyForecastList
}
