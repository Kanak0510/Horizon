package com.example.horizon.ui.weatherDetail

import com.example.horizon.domain.models.CurrentWeatherDetails
import com.example.horizon.domain.models.HourlyForecast
import com.example.horizon.domain.models.PrecipitationProbability
import com.example.horizon.domain.models.SingleWeatherDetail

/**
 * A UI state class that represents the current UI state of the [WeatherDetailScreen].
 */
data class WeatherDetailScreenUiState(
    val isLoading: Boolean = false,
    val isPreviouslySavedLocation: Boolean = false,
    val weatherDetailsOfChosenLocation: CurrentWeatherDetails? = null,
    val weatherSummaryText: String? = "",
    val errorMessage: String? = null,
    val precipitationProbabilities: List<PrecipitationProbability> = emptyList(),
    val hourlyForecasts: List<HourlyForecast> = emptyList(),
    val additionalWeatherInfoItems: List<SingleWeatherDetail> = emptyList()
)