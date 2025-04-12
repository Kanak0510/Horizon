package com.project.horizon.data.remote.weather.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A data class that models the response received when requesting the current
 * weather of a location, using Kotlinx Serialization.
 *
 * Kotlinx Serialization Notes:
 * - It is a multiplatform, compile-time JSON parser and serializer.
 * - It avoids reflection, resulting in better runtime performance.
 * - Fully supports Kotlin-specific features like nullability, default values, and data classes.
 * - No need for additional annotation processors such as KAPT.
 *
 * @property currentWeather The current weather data for the requested location.
 * @property latitude The latitude of the queried location.
 * @property longitude The longitude of the queried location.
 */
@Serializable
data class CurrentWeatherResponse(
    @SerialName("current_weather") val currentWeather: CurrentWeather,
    val latitude: String,
    val longitude: String
) {
    /**
     * A data class representing the current weather conditions at a specific location.
     *
     * @property temperature The current temperature in degrees Celsius.
     * @property isDay Indicator of whether it's day (1) or night (0) at the location.
     * @property weatherCode Numeric code that represents the current weather condition
     *                        (e.g., clear, rain, snow — based on a predefined code set).
     */
    @Serializable
    data class CurrentWeather(
        val temperature: Double,
        @SerialName("is_day") val isDay: Int,
        @SerialName("weathercode") val weatherCode: Int
    )
}
