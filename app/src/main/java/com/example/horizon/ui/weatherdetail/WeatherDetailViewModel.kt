package com.example.horizon.ui.weatherdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horizon.data.repositories.textgenerator.GenerativeTextRepository
import com.example.horizon.data.repositories.weather.WeatherRepository
import com.example.horizon.data.repositories.weather.fetchHourlyForecastsForNext24Hours
import com.example.horizon.data.repositories.weather.fetchPrecipitationProbabilitiesForNext24hours
import com.example.horizon.ui.navigation.HorizonNavigationDestinations.WeatherDetailScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [WeatherDetailViewModel] is responsible for managing UI state and data retrieval
 * for the [WeatherDetailScreen]. It fetches and exposes current weather details, forecasts,
 * AI-generated summaries, and saved location status.
 *
 * @property savedStateHandle A handle to access navigation arguments.
 * @property weatherRepository Repository for weather data operations.
 * @property generativeTextRepository Repository for generating textual weather summaries.
 */
@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val weatherRepository: WeatherRepository,
    private val generativeTextRepository: GenerativeTextRepository
) : ViewModel() {

    private val latitude: String = savedStateHandle[WeatherDetailScreen.NAV_ARG_LATITUDE]!!
    private val longitude: String = savedStateHandle[WeatherDetailScreen.NAV_ARG_LONGITUDE]!!
    private val nameOfLocation: String = savedStateHandle[WeatherDetailScreen.NAV_ARG_NAME_OF_LOCATION]!!

    private val _uiState = MutableStateFlow(WeatherDetailScreenUiState())
    val uiState: StateFlow<WeatherDetailScreenUiState> = _uiState

    init {
        observeSavedLocationStatus()
        fetchWeatherDetailsOnInit()
    }

    /**
     * Observes whether the current location is already saved by the user and updates the UI state accordingly.
     */
    private fun observeSavedLocationStatus() {
        weatherRepository.getSavedLocationsListStream()
            .map { savedLocations -> savedLocations.any { it.nameOfLocation == nameOfLocation } }
            .onEach { isSaved ->
                _uiState.update { it.copy(isPreviouslySavedLocation = isSaved) }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Initiates data loading when the ViewModel is created.
     */
    private fun fetchWeatherDetailsOnInit() {
        viewModelScope.launch {
            try {
                fetchWeatherDetailsAndUpdateState()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = DEFAULT_ERROR_MESSAGE)
                }
            }
        }
    }

    /**
     * Fetches weather data concurrently and updates the UI state.
     */
    private suspend fun fetchWeatherDetailsAndUpdateState() = coroutineScope {
        _uiState.update { it.copy(isLoading = true, isWeatherSummaryTextLoading = true) }

        val weatherDetailsDeferred = async {
            weatherRepository.fetchWeatherForLocation(
                nameOfLocation = nameOfLocation,
                latitude = latitude,
                longitude = longitude
            ).getOrThrow()
        }

        val summaryDeferred = async {
            generativeTextRepository.generateTextForWeatherDetails(weatherDetailsDeferred.await()).getOrNull()
        }

        val precipitationDeferred = async {
            weatherRepository.fetchPrecipitationProbabilitiesForNext24hours(
                latitude = latitude,
                longitude = longitude
            ).getOrThrow()
        }

        val hourlyForecastsDeferred = async {
            weatherRepository.fetchHourlyForecastsForNext24Hours(
                latitude = latitude,
                longitude = longitude
            ).getOrThrow()
        }

        val additionalWeatherInfoDeferred = async {
            weatherRepository.fetchAdditionalWeatherInfoItemsListForCurrentDay(
                latitude = latitude,
                longitude = longitude
            ).getOrThrow()
        }

        // Update UI with core weather data
        _uiState.update {
            it.copy(
                isLoading = false,
                weatherDetailsOfChosenLocation = weatherDetailsDeferred.await(),
                precipitationProbabilities = precipitationDeferred.await(),
                hourlyForecasts = hourlyForecastsDeferred.await(),
                additionalWeatherInfoItems = additionalWeatherInfoDeferred.await()
            )
        }

        // Update summary text separately as it may take longer to generate
        _uiState.update {
            it.copy(
                isWeatherSummaryTextLoading = false,
                weatherSummaryText = summaryDeferred.await()
            )
        }
    }

    /**
     * Adds the current location to the saved locations list in the repository.
     */
    fun addLocationToSavedLocations() {
        viewModelScope.launch {
            weatherRepository.saveWeatherLocation(
                nameOfLocation = nameOfLocation,
                latitude = latitude,
                longitude = longitude
            )
        }
    }

    companion object {
        private const val DEFAULT_ERROR_MESSAGE =
            "Oops! An error occurred while fetching the weather details. Please try again."
    }
}
