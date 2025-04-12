package com.project.horizon.ui.home

import com.project.horizon.domain.models.location.LocationAutofillSuggestion
import com.project.horizon.domain.models.weather.BriefWeatherDetails
import com.project.horizon.domain.models.weather.HourlyForecast

/**
 * Represents the UI state of the [HomeScreen], capturing loading states,
 * fetched data, and any error indicators.
 *
 * This state is typically managed by a ViewModel and observed by the UI.
 */
data class HomeScreenUiState(
    /** Whether autofill suggestions are currently being loaded. */
    val isLoadingAutofillSuggestions: Boolean = false,

    /** Whether saved locations are currently being loaded. */
    val isLoadingSavedLocations: Boolean = false,

    /** Whether weather details for the current location are being loaded. */
    val isLoadingWeatherDetailsOfCurrentLocation: Boolean = false,

    /** Whether there was an error fetching weather data for the current location. */
    val errorFetchingWeatherForCurrentLocation: Boolean = false,

    /** Whether there was an error fetching weather data for saved locations. */
    val errorFetchingWeatherForSavedLocations: Boolean = false,

    /** Whether there was an error fetching location autofill suggestions. */
    val errorFetchingAutofillSuggestions: Boolean = false,

    /** Weather details for the user's current location, if available. */
    val weatherDetailsOfCurrentLocation: BriefWeatherDetails? = null,

    /** Hourly forecast data for the current location, if available. */
    val hourlyForecastsForCurrentLocation: List<HourlyForecast>? = null,

    /** Autofill suggestions based on user's search or input. */
    val autofillSuggestions: List<LocationAutofillSuggestion> = emptyList(),

    /** Weather details for all saved (pinned) locations. */
    val weatherDetailsOfSavedLocations: List<BriefWeatherDetails> = emptyList()
)
