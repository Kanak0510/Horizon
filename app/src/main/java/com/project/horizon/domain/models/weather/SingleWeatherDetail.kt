package com.project.horizon.domain.models.weather

import androidx.annotation.DrawableRes
import com.project.horizon.R
import com.project.horizon.data.remote.weather.models.AdditionalDailyForecastVariablesResponse
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/**
 * Represents a single piece of weather detail (e.g., min temp, sunrise time).
 *
 * @property name The name/label of the weather detail.
 * @property value The corresponding value, formatted as a string.
 * @property iconResId The drawable resource ID for the associated icon.
 */
data class SingleWeatherDetail(
    val name: String,
    val value: String,
    @DrawableRes val iconResId: Int
)

/**
 * Maps an instance of [AdditionalDailyForecastVariablesResponse] to a list of [SingleWeatherDetail].
 *
 * @param timeFormat Optional formatter for sunrise and sunset times. Default is "hh : mm a".
 */
fun AdditionalDailyForecastVariablesResponse.toSingleWeatherDetailList(
    timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("hh : mm a")
): List<SingleWeatherDetail> = additionalForecastedVariables.toSingleWeatherDetailList(
    timezone = timezone,
    timeFormat = timeFormat
)

/**
 * Maps an instance of [AdditionalDailyForecastVariablesResponse.AdditionalForecastedVariables]
 * to a list of [SingleWeatherDetail], using the given [timezone] and [timeFormat].
 *
 * ⚠️ Assumes each list inside this class contains exactly one value.
 */
private fun AdditionalDailyForecastVariablesResponse.AdditionalForecastedVariables.toSingleWeatherDetailList(
    timezone: String,
    timeFormat: DateTimeFormatter
): List<SingleWeatherDetail> {
    require(minTemperatureForTheDay.size == 1) {
        "Only one day's data is supported. Ensure each list has exactly one element."
    }

    val apparentTemperature = (
            minApparentTemperature.first().roundToInt() +
                    maxApparentTemperature.first().roundToInt()
            ) / 2

    val sunriseTime = Instant.ofEpochSecond(sunrise.first())
        .atZone(ZoneId.of(timezone))
        .toLocalTime()
        .format(timeFormat)

    val sunsetTime = Instant.ofEpochSecond(sunset.first())
        .atZone(ZoneId.of(timezone))
        .toLocalTime()
        .format(timeFormat)

    return listOf(
        SingleWeatherDetail("Min Temp", "${minTemperatureForTheDay.first().roundToInt()}º", R.drawable.ic_thermometer),
        SingleWeatherDetail("Max Temp", "${maxTemperatureForTheDay.first().roundToInt()}º", R.drawable.ic_thermometer),
        SingleWeatherDetail("Sunrise", sunriseTime, R.drawable.ic_sunrise),
        SingleWeatherDetail("Sunset", sunsetTime, R.drawable.ic_sunset),
        SingleWeatherDetail("Feels Like", "${apparentTemperature}º", R.drawable.ic_thermometer),
        SingleWeatherDetail("Max UV Index", maxUvIndex.first().toString(), R.drawable.ic_uv_index),
        SingleWeatherDetail("Wind Direction", "${dominantWindDirection.first()}º", R.drawable.ic_wind_direction),
        SingleWeatherDetail("Wind Speed", "${windSpeed.first()} Km/h", R.drawable.ic_wind_speed)
    )
}
