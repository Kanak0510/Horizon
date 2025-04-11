package com.example.horizon.data.remote.weather.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A response object that contains additional daily weather forecast variables for a specific location.
 *
 * @property timezone The timezone associated with the forecast data.
 * @property additionalForecastedVariables A collection of forecasted weather variables.
 */
@Serializable
data class AdditionalDailyForecastVariablesResponse(
    @SerialName("timezone") val timezone: String,
    @SerialName("daily") val additionalForecastedVariables: AdditionalForecastedVariables
) {
    /**
     * A data class containing various additional weather variables forecasted for each day.
     *
     * @property minTemperatureForTheDay List of minimum temperatures (°C) for each forecasted day.
     * @property maxTemperatureForTheDay List of maximum temperatures (°C) for each forecasted day.
     * @property maxApparentTemperature List of maximum "feels like" temperatures (°C) for each day.
     * @property minApparentTemperature List of minimum "feels like" temperatures (°C) for each day.
     * @property sunrise List of sunrise times represented as epoch timestamps.
     * @property sunset List of sunset times represented as epoch timestamps.
     * @property maxUvIndex List of maximum UV index values for each day.
     * @property dominantWindDirection List of dominant wind directions (in degrees) for each day.
     * @property windSpeed List of maximum wind speeds (km/h or m/s depending on source) for each day.
     */
    @Serializable
    data class AdditionalForecastedVariables(
        @SerialName("temperature_2m_min") val minTemperatureForTheDay: List<Double>,
        @SerialName("temperature_2m_max") val maxTemperatureForTheDay: List<Double>,
        @SerialName("apparent_temperature_max") val maxApparentTemperature: List<Double>,
        @SerialName("apparent_temperature_min") val minApparentTemperature: List<Double>,
        @SerialName("sunrise") val sunrise: List<Long>,
        @SerialName("sunset") val sunset: List<Long>,
        @SerialName("uv_index_max") val maxUvIndex: List<Double>,
        @SerialName("winddirection_10m_dominant") val dominantWindDirection: List<Int>,
        @SerialName("windspeed_10m_max") val windSpeed: List<Double>
    )
}
