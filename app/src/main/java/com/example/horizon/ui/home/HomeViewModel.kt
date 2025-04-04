package com.example.horizon.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horizon.data.remote.location.ReverseGeocoder
import com.example.horizon.data.repositories.location.LocationServicesRepository
import com.example.horizon.data.repositories.weather.WeatherRepository
import com.example.horizon.data.repositories.weather.fetchHourlyForecastsForNext24Hours
import com.example.horizon.domain.location.CurrentLocationProvider
import com.example.horizon.domain.models.BriefWeatherDetails
import com.example.horizon.domain.models.Coordinates
import com.example.horizon.domain.models.CurrentWeatherDetails
import com.example.horizon.domain.models.HourlyForecast
import com.example.horizon.domain.models.LocationAutofillSuggestion
import com.example.horizon.domain.models.SavedLocation
import com.example.horizon.domain.models.toBriefWeatherDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
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
    private val isLoadingAutofillSuggestions = MutableStateFlow(false)
    private val isLoadingSavedLocations = MutableStateFlow(false)
    private val coordinatesOfCurrentLocation = MutableStateFlow<Coordinates?>(null)

    val weatherDetailsOfCurrentLocation: StateFlow<BriefWeatherDetails?> =
        coordinatesOfCurrentLocation.filterNotNull()
            .map { coordinates ->
                val nameOfLocation = reverseGeocoder.getLocationNameForCoordinates(
                    coordinates.latitude.toDouble(),
                    coordinates.longitude.toDouble()
                ).getOrNull() ?: return@map null // todo : exception handling
                weatherRepository.fetchWeatherForLocation(
                    nameOfLocation = nameOfLocation,
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude
                ).getOrNull()?.toBriefWeatherDetails() // todo : exception handling
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(300),
                initialValue = null
            )

    val hourlyForecastsForCurrentLocation: StateFlow<List<HourlyForecast>?> =
        coordinatesOfCurrentLocation.filterNotNull()
            .map { coordinates ->
                weatherRepository.fetchHourlyForecastsForNext24Hours(
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude
                ).getOrNull() // todo : exception handling
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(300),
                initialValue = null
            )

    // To understand why this flow is converted into a state flow, see the explanation above. The UiState Property below.
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private val autofillSuggestionsResults: Flow<Result<List<LocationAutofillSuggestion>>> =
        currentSearchQuery.debounce(250)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .mapLatest { query ->
                isLoadingAutofillSuggestions.value = true
                locationServicesRepository.fetchSuggestedPlacesForQuery(query)
                    .also { isLoadingAutofillSuggestions.value = false }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(300),
                initialValue = Result.success(emptyList())
            )

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState as StateFlow<HomeScreenUiState>

    // A Cache that stores the CurrentWeatherDetails of a specific SavedLocation
    private var currentWeatherDetailsCache = mutableMapOf<SavedLocation, CurrentWeatherDetails>()
    private var recentlyDeletedItem: BriefWeatherDetails? = null

    init {
        weatherRepository
            .getSavedLocationsListStream()
            .map { savedLocations ->
                _uiState.update { it.copy(isLoadingSavedLocations = true) }
                fetchCurrentWeatherDetailsWithCache(savedLocations.toSet()) // todo handle exceptions
            }
            .map { currentWeatherDetails ->
                currentWeatherDetails.map { it.toBriefWeatherDetails() }
                    .sortedBy { it.nameOfLocation }
            }
            .onEach { weatherDetailsOfSavedLocations ->
                _uiState.update {
                    it.copy(
                        isLoadingSavedLocations = false,
                        weatherDetailsOfSavedLocations = weatherDetailsOfSavedLocations
                    )
                }
            }
            .launchIn(viewModelScope) // todo take care of exceptions

        @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
        currentSearchQuery.debounce(250)
            .distinctUntilChanged()
            .mapLatest { query ->
                if (query.isBlank()) return@mapLatest Result.success(emptyList())
                _uiState.update { it.copy(isLoadingSuggestions = true) }
                locationServicesRepository.fetchSuggestedPlacesForQuery(query)
            }
            .filter { it.isSuccess }
            .map { it.getOrThrow() } // todo exception handling
            .onEach { autoFillSuggestions ->
                _uiState.update {
                    it.copy(
                        isLoadingSuggestions = false,
                        autofillSuggestions = autoFillSuggestions
                    )
                }
            }
            .launchIn(viewModelScope)
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
     * Used to fetch a list of [CurrentWeatherDetails] for all the [savedLocations] efficiently
     * using the [currentWeatherDetailsCache]
     */
    private suspend fun fetchCurrentWeatherDetailsWithCache(savedLocations: Set<SavedLocation>): List<CurrentWeatherDetails> {
        // Remove locations in the cache that have been deleted by the user
        val removedLocations = currentWeatherDetailsCache.keys subtract savedLocations
        for (removedLocation in removedLocations) {
            currentWeatherDetailsCache.remove(removedLocation)
        }
        // Only fetch weather details of the items that are not in cache
        val locationsNotInCache = savedLocations subtract currentWeatherDetailsCache.keys
        for (savedLocationNotInCache in locationsNotInCache) {
            weatherRepository.fetchWeatherForLocation(
                nameOfLocation = savedLocationNotInCache.nameOfLocation,
                latitude = savedLocationNotInCache.coordinates.latitude,
                longitude = savedLocationNotInCache.coordinates.longitude
            ).getOrThrow().also { currentWeatherDetailsCache[savedLocationNotInCache] = it }
        }
        return  currentWeatherDetailsCache.values.toList()
    }

    fun fetchWeatherForCurrentUserLocation() {
        viewModelScope.launch {
            val coordinatesResult =
                currentLocationProvider.getCurrentLocation().getOrNull() ?: return@launch
            coordinatesOfCurrentLocation.value = coordinatesResult
        }

    }
}