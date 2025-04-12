package com.project.horizon.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.horizon.data.remote.location.ReverseGeocoder
import com.project.horizon.data.repositories.location.LocationServicesRepository
import com.project.horizon.data.repositories.weather.WeatherRepository
import com.project.horizon.data.repositories.weather.fetchHourlyForecastsForNext24Hours
import com.project.horizon.domain.location.CurrentLocationProvider
import com.project.horizon.domain.models.location.SavedLocation
import com.project.horizon.domain.models.weather.BriefWeatherDetails
import com.project.horizon.domain.models.weather.CurrentWeatherDetails
import com.project.horizon.domain.models.weather.toBriefWeatherDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing and exposing UI state for the Home screen.
 * Handles saved locations, weather data, current user location, and location-based suggestions.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val currentLocationProvider: CurrentLocationProvider,
    private val reverseGeocoder: ReverseGeocoder,
    private val locationServicesRepository: LocationServicesRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val currentSearchQuery = MutableStateFlow("")
    private val isCurrentlyRetryingToFetchSavedLocation = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState

    // Cache to store weather details of saved locations to avoid redundant fetches
    private val currentWeatherDetailsCache = mutableMapOf<SavedLocation, CurrentWeatherDetails>()

    private var recentlyDeletedItem: BriefWeatherDetails? = null

    init {
        observeSavedLocations()
        observeSearchQuerySuggestions()
    }

    /**
     * Observes the saved locations list and updates weather data using caching.
     */
    private fun observeSavedLocations() {
        combine(
            weatherRepository.getSavedLocationsListStream(),
            isCurrentlyRetryingToFetchSavedLocation
        ) { savedLocations, _ -> savedLocations }
            .onEach {
                _uiState.update {
                    it.copy(
                        isLoadingSavedLocations = true,
                        errorFetchingWeatherForSavedLocations = false
                    )
                }
            }
            .map { fetchCurrentWeatherDetailsWithCache(it) }
            .onEach { result ->
                val details = result.getOrNull()
                _uiState.update {
                    it.copy(
                        isLoadingSavedLocations = false,
                        weatherDetailsOfSavedLocations = details ?: emptyList(),
                        errorFetchingWeatherForSavedLocations = details == null
                    )
                }
                isCurrentlyRetryingToFetchSavedLocation.update { false }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Observes and debounces user input for location search queries.
     * Triggers suggestion fetch from the repository.
     */
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeSearchQuerySuggestions() {
        currentSearchQuery
            .debounce(250)
            .distinctUntilChanged()
            .mapLatest { query ->
                if (query.isBlank()) return@mapLatest Result.success(emptyList())
                _uiState.update {
                    it.copy(
                        isLoadingAutofillSuggestions = true,
                        errorFetchingAutofillSuggestions = false
                    )
                }
                locationServicesRepository.fetchSuggestedPlacesForQuery(query)
            }
            .onEach { result ->
                val suggestions = result.getOrNull()
                _uiState.update {
                    it.copy(
                        isLoadingAutofillSuggestions = false,
                        autofillSuggestions = suggestions ?: emptyList(),
                        errorFetchingAutofillSuggestions = suggestions == null
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Sets the user input query for location suggestion fetching.
     *
     * @param searchQuery User's search input string
     */
    fun setSearchQueryForSuggestionsGeneration(searchQuery: String) {
        currentSearchQuery.value = searchQuery
    }

    /**
     * Retries loading saved location weather data if a previous attempt failed.
     */
    fun retryFetchingSavedLocations() {
        if (!isCurrentlyRetryingToFetchSavedLocation.value) {
            isCurrentlyRetryingToFetchSavedLocation.update { true }
        }
    }

    /**
     * Deletes a weather location and temporarily stores it for undo functionality.
     *
     * @param briefWeatherDetails The location to delete
     */
    fun deleteSavedWeatherLocation(briefWeatherDetails: BriefWeatherDetails) {
        recentlyDeletedItem = briefWeatherDetails
        viewModelScope.launch {
            weatherRepository.deleteWeatherLocationFromSavedItems(briefWeatherDetails)
        }
    }

    /**
     * Attempts to restore the most recently deleted weather location, if available.
     */
    fun restoreRecentlyDeletedItem() {
        recentlyDeletedItem?.let {
            viewModelScope.launch {
                weatherRepository.tryRestoringDeletedWeatherLocation(it.nameOfLocation)
            }
        }
    }

    /**
     * Fetches and updates the weather information for the user's current device location.
     */
    fun fetchWeatherForCurrentUserLocation() {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            _uiState.update {
                it.copy(
                    isLoadingWeatherDetailsOfCurrentLocation = false,
                    errorFetchingWeatherForCurrentLocation = true
                )
            }
        }

        viewModelScope.launch(exceptionHandler) {
            _uiState.update {
                it.copy(
                    isLoadingWeatherDetailsOfCurrentLocation = true,
                    errorFetchingWeatherForCurrentLocation = false
                )
            }

            val coordinates = currentLocationProvider.getCurrentLocation().getOrThrow()
            val nameOfLocation = reverseGeocoder.getLocationNameForCoordinates(
                coordinates.latitude.toDouble(),
                coordinates.longitude.toDouble()
            ).getOrThrow()

            val weatherDeferred = async {
                weatherRepository.fetchWeatherForLocation(
                    nameOfLocation,
                    coordinates.latitude,
                    coordinates.longitude
                ).getOrThrow().toBriefWeatherDetails()
            }

            val forecastDeferred = async {
                weatherRepository.fetchHourlyForecastsForNext24Hours(
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude
                ).getOrThrow()
            }

            _uiState.update {
                it.copy(
                    isLoadingWeatherDetailsOfCurrentLocation = false,
                    errorFetchingWeatherForCurrentLocation = false,
                    weatherDetailsOfCurrentLocation = weatherDeferred.await(),
                    hourlyForecastsForCurrentLocation = forecastDeferred.await()
                )
            }
        }
    }

    /**
     * Efficiently fetches weather details for each saved location using cache.
     *
     * @param savedLocations The list of saved user locations
     * @return A [Result] containing a list of [BriefWeatherDetails] if successful
     */
    private suspend fun fetchCurrentWeatherDetailsWithCache(
        savedLocations: List<SavedLocation>
    ): Result<List<BriefWeatherDetails>?> {
        val savedLocationsSet = savedLocations.toSet()
        val removedLocations = currentWeatherDetailsCache.keys - savedLocationsSet
        removedLocations.forEach { currentWeatherDetailsCache.remove(it) }

        val locationsToFetch = savedLocationsSet - currentWeatherDetailsCache.keys
        for (location in locationsToFetch) {
            try {
                val details = weatherRepository.fetchWeatherForLocation(
                    nameOfLocation = location.nameOfLocation,
                    latitude = location.coordinates.latitude,
                    longitude = location.coordinates.longitude
                ).getOrThrow()
                currentWeatherDetailsCache[location] = details
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                return Result.failure(e)
            }
        }

        return Result.success(
            currentWeatherDetailsCache.values.map { it.toBriefWeatherDetails() }
        )
    }
}
