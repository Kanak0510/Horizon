package com.example.horizon.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horizon.data.remote.location.ReverseGeocoder
import com.example.horizon.data.repositories.location.LocationServicesRepository
import com.example.horizon.data.repositories.weather.WeatherRepository
import com.example.horizon.data.repositories.weather.fetchHourlyForecastsForNext24Hours
import com.example.horizon.domain.location.CurrentLocationProvider
import com.example.horizon.domain.models.SavedLocation
import com.example.horizon.domain.models.weather.BriefWeatherDetails
import com.example.horizon.domain.models.weather.CurrentWeatherDetails
import com.example.horizon.domain.models.weather.toBriefWeatherDetails
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
    val uiState = _uiState as StateFlow<HomeScreenUiState>

    // A Cache that stores the CurrentWeatherDetails of a specific SavedLocation
    private var currentWeatherDetailsCache = mutableMapOf<SavedLocation, CurrentWeatherDetails>()
    private var recentlyDeletedItem: BriefWeatherDetails? = null

    init {
        // Saved Location Stream
        combine(
            weatherRepository.getSavedLocationsListStream(),
            isCurrentlyRetryingToFetchSavedLocation
        ) { savedLocations, _ ->
            savedLocations
        }.onEach {
            _uiState.update {
                it.copy(
                    isLoadingSavedLocations = true,
                    errorFetchingWeatherForSavedLocations = false
                )
            }
        }.map { savedLocations ->
            fetchCurrentWeatherDetailsWithCache(savedLocations)
        }.onEach { weatherDetailsOfSavedLocationsResult ->
            val weatherDetailsOfSavedLocations =
                weatherDetailsOfSavedLocationsResult.getOrNull()
            _uiState.update {
                it.copy(
                    isLoadingSavedLocations = false,
                    weatherDetailsOfSavedLocations = weatherDetailsOfSavedLocations ?: emptyList(),
                    errorFetchingWeatherForSavedLocations = weatherDetailsOfSavedLocations == null
                )
            }
            isCurrentlyRetryingToFetchSavedLocation.update { false }
        }.launchIn(viewModelScope)

        // Suggestion for Current Search Query Stream
        @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
        currentSearchQuery.debounce(250)
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
            .onEach { autofillSuggestionsResult ->
                val autofillSuggestions = autofillSuggestionsResult.getOrNull()
                _uiState.update {
                    it.copy(
                        isLoadingAutofillSuggestions = false,
                        autofillSuggestions = autofillSuggestions ?: emptyList(),
                        errorFetchingAutofillSuggestions = autofillSuggestions == null
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun retryFetchingSavedLocations() {
        val isCurrentlyRetrying = isCurrentlyRetryingToFetchSavedLocation.value
        if (isCurrentlyRetrying) return
        isCurrentlyRetryingToFetchSavedLocation.update { true }
    }

    /**
     * Used to set the [searchQuery] for which the suggestions should be generated.
     */
    fun setSearchQueryForSuggestionsGeneration(searchQuery: String) {
        currentSearchQuery.value = searchQuery
    }

    fun deleteSavedWeatherLocation(briefWeatherDetails: BriefWeatherDetails) {
        recentlyDeletedItem = briefWeatherDetails
        viewModelScope.launch {
            weatherRepository.deleteWeatherLocationFromSavedItems(briefWeatherDetails)
        }
    }

    fun restoreRecentlyDeletedItem() {
        recentlyDeletedItem?.let {
            viewModelScope.launch { weatherRepository.tryRestoringDeletedWeatherLocation(it.nameOfLocation) }
        }
    }

    /**
     * Used to fetch a list of [BriefWeatherDetails] for all the [savedLocations] efficiently
     * using the [currentWeatherDetailsCache]
     */
    private suspend fun fetchCurrentWeatherDetailsWithCache(savedLocations: List<SavedLocation>): Result<List<BriefWeatherDetails>?> {
        val savedLocationsSet = savedLocations.toSet()
        // Remove locations in the cache that have been deleted by the user
        val removedLocations = currentWeatherDetailsCache.keys subtract savedLocationsSet
        for (removedLocation in removedLocations) {
            currentWeatherDetailsCache.remove(removedLocation)
        }
        // Only fetch weather details of the items that are not in cache
        val locationsNotInCache = savedLocationsSet subtract currentWeatherDetailsCache.keys
        for (savedLocationNotInCache in locationsNotInCache) {
            try {
                weatherRepository.fetchWeatherForLocation(
                    nameOfLocation = savedLocationNotInCache.nameOfLocation,
                    latitude = savedLocationNotInCache.coordinates.latitude,
                    longitude = savedLocationNotInCache.coordinates.longitude
                ).getOrThrow().also { currentWeatherDetailsCache[savedLocationNotInCache] = it }
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                return Result.failure(exception)
            }
        }
        return Result.success(
            currentWeatherDetailsCache.values.toList().map { it.toBriefWeatherDetails() })
    }

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

            val weatherDetailsForCurrentLocation = async {
                weatherRepository.fetchWeatherForLocation(
                    nameOfLocation = nameOfLocation,
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude
                ).getOrThrow().toBriefWeatherDetails()
            }

            val hourlyForecastsForCurrentLocation = async {
                weatherRepository.fetchHourlyForecastsForNext24Hours(
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude
                ).getOrThrow()
            }
            _uiState.update {
                it.copy(
                    isLoadingWeatherDetailsOfCurrentLocation = false,
                    errorFetchingWeatherForCurrentLocation = false,
                    weatherDetailsOfCurrentLocation = weatherDetailsForCurrentLocation.await(),
                    hourlyForecastsForCurrentLocation = hourlyForecastsForCurrentLocation.await(),
                )
            }
        }
    }
}