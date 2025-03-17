package com.example.horizon.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horizon.data.repositories.location.LocationServicesRepository
import com.example.horizon.data.repositories.weather.WeatherRepository
import com.example.horizon.domain.models.BriefWeatherDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationServicesRepository: LocationServicesRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val currentSearchQuery = MutableStateFlow("")
    private val _uiState = MutableStateFlow(UiState.IDLE)
    val uiState = _uiState as StateFlow<UiState>

    @OptIn(FlowPreview::class)
    val currentSuggestions = currentSearchQuery.debounce(250)
        .filter { it.isNotBlank() }
        .map { query ->
            _uiState.value = UiState.LOADING_SUGGESTIONS
            locationServicesRepository.fetchSuggestedPlacesForQuery(query)
                .also { _uiState.value = UiState.IDLE }
        }
        .filter { it.isSuccess }
        .map { it.getOrThrow() }

    private val _weatherDetailsOfSavedLocations = MutableStateFlow<List<BriefWeatherDetails>>(emptyList())
    val weatherDetailsOfSavedLocations = _weatherDetailsOfSavedLocations as StateFlow<List<BriefWeatherDetails>>

    init {
        _uiState.value = UiState.LOADING_SAVED_LOCATIONS
        weatherRepository
            .getWeatherStreamForPreviouslySavedLocations()
            .onEach {
                if (_uiState.value == UiState.LOADING_SAVED_LOCATIONS) {
                    _uiState.value = UiState.IDLE
                }
                _weatherDetailsOfSavedLocations.value = it
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
        viewModelScope.launch {
            weatherRepository.deleteWeatherLocationFromSavedItems(briefWeatherDetails)
        }
    }

    /**
     * An enum that contains all possible UI states.
     */
    enum class UiState {
        IDLE,
        LOADING_SUGGESTIONS,
        LOADING_SAVED_LOCATIONS
    }
}