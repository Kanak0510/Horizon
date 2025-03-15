package com.example.horizon.ui.weather

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horizon.data.repositories.weather.WeatherRepository
import com.example.horizon.domain.models.WeatherDetails
import com.example.horizon.ui.navigation.HorizonNavigationDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val latitude: String =
        savedStateHandle[HorizonNavigationDestinations.WeatherDetailScreen.NAV_ARG_LATITUDE]!!
    private val longitude: String =
        savedStateHandle[HorizonNavigationDestinations.WeatherDetailScreen.NAV_ARG_LONGITUDE]!!

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState as StateFlow<UiState>

    init {
        viewModelScope.launch { fetchWeatherInfo() }
    }

    /**
     * Fetches weather information from the repository, ensuring that the [uiState] is
     * correctly updated.
     */
    private suspend fun fetchWeatherInfo() {
        _uiState.value = UiState.Loading
        val weatherDetails = weatherRepository.fetchWeatherForLocation(
            latitude = latitude,
            longitude = longitude
        ).getOrNull()
        _uiState.value = if (weatherDetails == null) UiState.Error
        else UiState.SuccessfullyLoaded(weatherDetails)
    }

    /**
     * A sealed class that contains all possible UI states.
     */
    sealed class UiState() {
        object Idle : UiState()
        object Loading : UiState()
        data class SuccessfullyLoaded(val weatherDetails: WeatherDetails) : UiState()
        object Error : UiState()
    }
}