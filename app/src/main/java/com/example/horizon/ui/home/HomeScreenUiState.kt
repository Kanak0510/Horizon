package com.example.horizon.ui.home

import com.example.horizon.domain.models.BriefWeatherDetails
import com.example.horizon.domain.models.HourlyForecast
import com.example.horizon.domain.models.LocationAutofillSuggestion

/**
 * A UI state class that represents the current UI state of the [HomeScreen].
 */
data class HomeScreenUiState(
    val isLoadingSuggestions: Boolean = false,
    val isLoadingSavedLocations: Boolean = false,
    val isLoadingWeatherDetailsOfCurrentLocation: Boolean = false,
    val weatherDetailsOfCurrentLocation: BriefWeatherDetails? = null,
    val hourlyForecastsForCurrentLocation: List<HourlyForecast>? = null,
    val autofillSuggestions: List<LocationAutofillSuggestion> = emptyList(),
    val weatherDetailsOfSavedLocations: List<BriefWeatherDetails> = emptyList()
)
