package com.example.horizon.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.horizon.domain.models.BriefWeatherDetails
import com.example.horizon.domain.models.LocationAutofillSuggestion
import com.example.horizon.ui.home.HomeScreen
import com.example.horizon.ui.home.HomeViewModel
import com.example.horizon.ui.weather.WeatherDetailScreen
import com.example.horizon.ui.weather.WeatherDetailViewModel

@Composable
fun HorizonNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = HorizonNavigationDestinations.HomeScreen.route
    ) {

        homeScreen(
            route = HorizonNavigationDestinations.HomeScreen.route,
            onSuggestionClick = {
                navController.navigateToWeatherDetailScreen(
                    latitude = it.coordinatesOfLocation.latitude,
                    longitude = it.coordinatesOfLocation.longitude
                )
            },
            onSavedLocationItemClick = {
                navController.navigateToWeatherDetailScreen(
                    latitude = it.latitude,
                    longitude = it.longitude
                )
            }
        )

        weatherDetailScreen(route = HorizonNavigationDestinations.WeatherDetailScreen.route)
    }
}

private fun NavGraphBuilder.homeScreen(
    route: String,
    onSuggestionClick: (suggestion: LocationAutofillSuggestion) -> Unit,
    onSavedLocationItemClick: (BriefWeatherDetails) -> Unit
) {
    composable(route = route) {
        val viewModel = hiltViewModel<HomeViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val suggestionsForCurrentQuery by viewModel.currentSuggestions
            .collectAsStateWithLifecycle(initialValue = emptyList())

        HomeScreen(
            modifier = Modifier.fillMaxSize(),
            weatherDetailsOfSavedLocations = emptyList(), // todo
            suggestionsForSearchQuery = suggestionsForCurrentQuery,
            isSuggestionsListLoading = uiState == HomeViewModel.UiState.LOADING_SUGGESTIONS,
            onSuggestionClick = onSuggestionClick,
            onSearchQueryChange = viewModel::setSearchQueryForSuggestionsGeneration,
            onSavedLocationItemClick = onSavedLocationItemClick
        )
    }
}

fun NavGraphBuilder.weatherDetailScreen(route: String) {
    composable(route) {
        val viewModel = hiltViewModel<WeatherDetailViewModel>()
        val weatherDetails by viewModel.weatherDetailsOfChosenLocation.collectAsStateWithLifecycle()
        WeatherDetailScreen(
            background = { }, // todo
            weatherDetails = weatherDetails,
            modifier = Modifier.fillMaxSize(),
            onBackButtonClick = {},
            onAddButtonClick = {},
            wasLocationPreviouslySaved = false // todo
        )
    }
}

private fun NavHostController.navigateToWeatherDetailScreen(
    latitude: String,
    longitude: String
) {
    val destination = HorizonNavigationDestinations.WeatherDetailScreen.buildRoute(
        latitude = latitude,
        longitude = longitude
    )
    navigate(destination)
}