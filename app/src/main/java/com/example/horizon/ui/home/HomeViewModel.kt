package com.example.horizon.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horizon.data.repositories.location.LocationServicesRepository
import com.example.horizon.data.repositories.weather.WeatherRepository
import com.example.horizon.domain.models.BriefWeatherDetails
import com.example.horizon.domain.models.CurrentWeatherDetails
import com.example.horizon.domain.models.LocationAutofillSuggestion
import com.example.horizon.domain.models.SavedLocation
import com.example.horizon.domain.models.toBriefWeatherDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationServicesRepository: LocationServicesRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val currentSearchQuery = MutableStateFlow("")
    private val isLoadingAutofillSuggestions = MutableStateFlow(false)
    private val isLoadingSavedLocations = MutableStateFlow(false)

    private val weatherDetailsOfSavedLocationsResults: Flow<Result<List<CurrentWeatherDetails>>> =
        weatherRepository.getSavedLocationsListStream()
            .map { savedLocations ->
                isLoadingSavedLocations.value = true
                fetchCurrentWeatherDetailsWithCache(savedLocations.toSet())
                    .also { isLoadingSavedLocations.value = false }
            }

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

    // IMPORTANT NOTE ABOUT THE COMBINE OPERATOR
    // By default, the combine operator waits for all flows to emit at least one value before it
    // starts combining them. So, the first call of the combine operator's transform block will happen
    // only when all the flows passed to the combine block have emitted at least a single value.
    //
    // For StateFlows, the initial value would be taken as the first emission. Since a normal
    // flow doesn't store a value in it, the combine block waits for the first emission before
    // calling the transform block for the first time. This implies that any update to either
    // state flows (marked below) will not get passed to the transform block unless the
    // other normal flows (marked below) emit at least one value.
    val uiState = combine(
        isLoadingSavedLocations, // State Flow
        isLoadingAutofillSuggestions, // State Flow
        weatherDetailsOfSavedLocationsResults, // Flow
        autofillSuggestionsResults // Flow converted to stateflow because this flow doesn't emit until the user starts searching
    ) { isLoadingSavedLocations, isLoadingAutofillSuggestions, weatherDetailsOfSavedLocationsResults, autofillSuggestionResults ->
        val autofillSuggestions = autofillSuggestionResults.getOrNull() ?: emptyList()
        val savedLocations = weatherDetailsOfSavedLocationsResults.getOrNull()
            ?.map { it.toBriefWeatherDetails() }
            ?.sortedBy { it.nameOfLocation } ?: emptyList()
        HomeScreenUiState(
            isLoadingSuggestions = isLoadingAutofillSuggestions,
            isLoadingSavedLocations = isLoadingSavedLocations,
            errorFetchingSavedLocations = weatherDetailsOfSavedLocationsResults.isFailure,
            errorFetchingAutofillSuggestions = autofillSuggestionResults.isFailure,
            autofillSuggestions = autofillSuggestions,
            weatherDetailsOfSavedLocations = savedLocations
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(300),
        initialValue = HomeScreenUiState(isLoadingSavedLocations = true)
    )

    // A Cache that stores the CurrentWeatherDetails of a specific SavedLocation
    private var currentWeatherDetailsCache = mutableMapOf<SavedLocation, CurrentWeatherDetails>()
    private var recentlyDeletedItem: BriefWeatherDetails? = null

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
    private suspend fun fetchCurrentWeatherDetailsWithCache(savedLocations: Set<SavedLocation>): Result<List<CurrentWeatherDetails>> {
        // Remove locations in the cache that have been deleted by the user
        val removedLocations = currentWeatherDetailsCache.keys subtract savedLocations
        for (removedLocation in removedLocations) {
            currentWeatherDetailsCache.remove(removedLocation)
        }
        // Only fetch weather details of the items that are not in cache
        val locationsNotInCache = savedLocations subtract currentWeatherDetailsCache.keys
        for (savedLocationNotInCache in locationsNotInCache) {
            currentWeatherDetailsCache[savedLocationNotInCache] =
                weatherRepository.fetchWeatherForLocation(
                    nameOfLocation = savedLocationNotInCache.nameOfLocation,
                    latitude = savedLocationNotInCache.coordinates.latitude,
                    longitude = savedLocationNotInCache.coordinates.longitude
                ).getOrElse { return Result.failure(it) }
        }
        return Result.success(currentWeatherDetailsCache.values.toList())
    }
}