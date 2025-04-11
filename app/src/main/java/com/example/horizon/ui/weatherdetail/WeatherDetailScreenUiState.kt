package com.example.horizon.ui.weatherdetail

import com.example.horizon.domain.models.weather.CurrentWeatherDetails
import com.example.horizon.domain.models.weather.HourlyForecast
import com.example.horizon.domain.models.weather.PrecipitationProbability
import com.example.horizon.domain.models.weather.SingleWeatherDetail

/**
 * Represents the complete UI state of the [WeatherDetailScreen].
 *
 * This state is used by the screen to render weather information, track loading and error
 * states, and update content such as weather summaries and forecasts.
 *
 * @property isLoading Indicates whether weather data is currently being loaded.
 * @property isPreviouslySavedLocation Whether the current location is already saved by the user.
 * @property weatherDetailsOfChosenLocation Contains detailed current weather data for the selected location.
 * @property isWeatherSummaryTextLoading Indicates whether the weather summary (e.g., AI-generated text) is being loaded.
 * @property weatherSummaryText A short, descriptive summary of the current weather conditions.
 * @property errorMessage Optional error message to be shown to the user in case of a failure.
 * @property precipitationProbabilities List of hourly precipitation probabilities for the selected location.
 * @property hourlyForecasts List of forecasted weather conditions for the next 24 hours.
 * @property additionalWeatherInfoItems Additional weather info such as UV index, wind, sunrise/sunset, etc.
 */
data class WeatherDetailScreenUiState(
    val isLoading: Boolean = true,
    val isPreviouslySavedLocation: Boolean = false,
    val weatherDetailsOfChosenLocation: CurrentWeatherDetails? = null,
    val isWeatherSummaryTextLoading: Boolean = false,
    val weatherSummaryText: String? = null,
    val errorMessage: String? = null,
    val precipitationProbabilities: List<PrecipitationProbability> = emptyList(),
    val hourlyForecasts: List<HourlyForecast> = emptyList(),
    val additionalWeatherInfoItems: List<SingleWeatherDetail> = emptyList()
)
