package com.example.horizon.data.remote.weather.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A data class representing the response containing hourly weather forecast information
 * for a specific location.
 *
 * @property latitude The latitude of the queried location.
 * @property longitude The longitude of the queried location.
 * @property hourlyForecast The [HourlyForecast] data which includes temperature, weather code,
 * and precipitation probability for each hour.
 */
@Serializable
data class HourlyWeatherInfoResponse(
    val latitude: String,
    val longitude: String,
    @SerialName("hourly") val hourlyForecast: HourlyForecast
) {
    /**
     * A data class representing hourly forecast details for a location.
     *
     * @property timestamps The list of timestamps (ISO 8601 format) representing the forecasted hours.
     * @property precipitationProbabilityPercentages A list of precipitation probability values (in percentage)
     * corresponding to each [timestamp].
     * @property weatherCodes A list of weather condition codes representing different weather states
     * (e.g., clear, rain, snow) for each hour.
     * @property temperatureForecasts A list of forecasted temperatures (in degrees Celsius) for each hour.
     */
    @Serializable
    data class HourlyForecast(
        @SerialName("time") val timestamps: List<String>,
        @SerialName("precipitation_probability") val precipitationProbabilityPercentages: List<Int> = emptyList(),
        @SerialName("weathercode") val weatherCodes: List<Int> = emptyList(),
        @SerialName("temperature_2m") val temperatureForecasts: List<Float> = emptyList()
    )
}
