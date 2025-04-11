package com.example.horizon.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.horizon.domain.models.location.LocationAutofillSuggestion
import com.example.horizon.domain.models.weather.BriefWeatherDetails
import com.example.horizon.ui.home.HomeScreen
import com.example.horizon.ui.home.HomeViewModel
import com.example.horizon.ui.weatherdetail.WeatherDetailScreen
import com.example.horizon.ui.weatherdetail.WeatherDetailViewModel
import kotlinx.coroutines.launch

/**
 * Root navigation composable for the Horizon app.
 * Hosts and manages all screen navigation using Jetpack Navigation Compose.
 *
 * @param navController The [NavHostController] to control navigation actions.
 */
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
                    nameOfLocation = it.nameOfLocation,
                    latitude = it.coordinatesOfLocation.latitude,
                    longitude = it.coordinatesOfLocation.longitude
                )
            },
            onSavedLocationItemClick = {
                navController.navigateToWeatherDetailScreen(
                    nameOfLocation = it.nameOfLocation,
                    latitude = it.coordinates.latitude,
                    longitude = it.coordinates.longitude
                )
            }
        )

        weatherDetailScreen(
            route = HorizonNavigationDestinations.WeatherDetailScreen.route,
            onBackButtonClick = navController::popBackStack
        )
    }
}

/**
 * Adds the Home screen composable to the navigation graph.
 */
private fun NavGraphBuilder.homeScreen(
    route: String,
    onSuggestionClick: (LocationAutofillSuggestion) -> Unit,
    onSavedLocationItemClick: (BriefWeatherDetails) -> Unit
) {
    composable(route = route) {
        val viewModel = hiltViewModel<HomeViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        val showUndoSnackbar = { deletedItem: BriefWeatherDetails ->
            coroutineScope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                val result = snackbarHostState.showSnackbar(
                    message = "${deletedItem.nameOfLocation} has been deleted",
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.restoreRecentlyDeletedItem()
                }
            }
        }

        HomeScreen(
            modifier = Modifier.fillMaxSize(),
            homeScreenUiState = uiState,
            snackbarHostState = snackbarHostState,
            onSavedLocationDismissed = {
                viewModel.deleteSavedWeatherLocation(it)
                showUndoSnackbar(it)
            },
            onSearchQueryChange = viewModel::setSearchQueryForSuggestionsGeneration,
            onSuggestionClick = onSuggestionClick,
            onSavedLocationItemClick = onSavedLocationItemClick,
            onLocationPermissionGranted = viewModel::fetchWeatherForCurrentUserLocation,
            onRetryFetchingWeatherForSavedLocations = viewModel::retryFetchingSavedLocations
        )
    }
}

/**
 * Adds the Weather Detail screen composable to the navigation graph.
 */
fun NavGraphBuilder.weatherDetailScreen(
    route: String,
    onBackButtonClick: () -> Unit
) {
    composable(route) {
        val viewModel = hiltViewModel<WeatherDetailViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        WeatherDetailScreen(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onBackButtonClick = onBackButtonClick,
            onSaveButtonClick = {
                viewModel.addLocationToSavedLocations()
                snackbarHostState.currentSnackbarData?.dismiss()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message = "Added to Saved Locations")
                }
            }
        )
    }
}

/**
 * Navigates to the Weather Detail screen with the specified [nameOfLocation], [latitude], and [longitude].
 */
private fun NavHostController.navigateToWeatherDetailScreen(
    nameOfLocation: String,
    latitude: String,
    longitude: String
) {
    val route = HorizonNavigationDestinations.WeatherDetailScreen.buildRoute(
        nameOfLocation = nameOfLocation,
        latitude = latitude,
        longitude = longitude
    )
    navigate(route) {
        launchSingleTop = true
    }
}
