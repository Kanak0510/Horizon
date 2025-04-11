package com.example.horizon.ui.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.horizon.domain.models.location.LocationAutofillSuggestion
import com.example.horizon.domain.models.weather.BriefWeatherDetails
import com.example.horizon.domain.models.weather.HourlyForecast
import com.example.horizon.ui.components.AutofillSuggestion
import com.example.horizon.ui.components.CompactWeatherCardWithHourlyForecast
import com.example.horizon.ui.components.SwipeToDismissCompactWeatherCard

/**
 * Displays the Home screen using the provided [HomeScreenUiState] to drive its content.
 * This is an overload that bridges ViewModel state to UI-level composable.
 *
 * @param homeScreenUiState The state that represents the UI of the Home screen.
 * @param snackbarHostState Host state to show Snackbars.
 * @param onSavedLocationDismissed Called when a saved location is swiped to dismiss.
 * @param onSearchQueryChange Callback when the user types into the search bar.
 * @param onSuggestionClick Called when an autofill suggestion is selected.
 * @param onSavedLocationItemClick Called when a saved location is clicked.
 * @param onLocationPermissionGranted Called when location permission is granted.
 * @param onRetryFetchingWeatherForSavedLocations Callback when retrying saved locations' weather.
 * @param modifier Modifier to be applied to the layout.
 * @param onRetryFetchingWeatherForCurrentLocation Optional callback for retrying current location fetch.
 */
@Composable
fun HomeScreen(
    homeScreenUiState: HomeScreenUiState,
    snackbarHostState: SnackbarHostState,
    onSavedLocationDismissed: (BriefWeatherDetails) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSuggestionClick: (LocationAutofillSuggestion) -> Unit,
    onSavedLocationItemClick: (BriefWeatherDetails) -> Unit,
    onLocationPermissionGranted: () -> Unit,
    onRetryFetchingWeatherForSavedLocations: () -> Unit,
    modifier: Modifier = Modifier,
    onRetryFetchingWeatherForCurrentLocation: () -> Unit = onLocationPermissionGranted
) {
    HomeScreen(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        weatherDetailsOfSavedLocations = homeScreenUiState.weatherDetailsOfSavedLocations,
        suggestionsForSearchQuery = homeScreenUiState.autofillSuggestions,
        isSuggestionsListLoading = homeScreenUiState.isLoadingAutofillSuggestions,
        isCurrentWeatherDetailsLoading = homeScreenUiState.isLoadingWeatherDetailsOfCurrentLocation,
        isWeatherForSavedLocationsLoading = homeScreenUiState.isLoadingSavedLocations,
        weatherOfCurrentUserLocation = homeScreenUiState.weatherDetailsOfCurrentLocation,
        hourlyForecastsOfCurrentUserLocation = homeScreenUiState.hourlyForecastsForCurrentLocation,
        errorFetchingWeatherForCurrentLocation = homeScreenUiState.errorFetchingWeatherForCurrentLocation,
        errorFetchingWeatherForSavedLocations = homeScreenUiState.errorFetchingWeatherForSavedLocations,
        errorLoadingAutofillSuggestions = homeScreenUiState.errorFetchingAutofillSuggestions,
        onRetryFetchingWeatherForCurrentLocation = onRetryFetchingWeatherForCurrentLocation,
        onRetryFetchingWeatherForSavedLocations = onRetryFetchingWeatherForSavedLocations,
        onSavedLocationDismissed = onSavedLocationDismissed,
        onSearchQueryChange = onSearchQueryChange,
        onSuggestionClick = onSuggestionClick,
        onSavedLocationItemClick = onSavedLocationItemClick,
        onLocationPermissionGranted = onLocationPermissionGranted
    )
}

/**
 * A home screen composable that displays a search bar with a list containing the current weather for
 * saved locations.
 *
 * @param modifier The modifier to be applied to the composable.
 * @param weatherDetailsOfSavedLocations The list of weather details of saved locations.
 */
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    weatherDetailsOfSavedLocations: List<BriefWeatherDetails>,
    suggestionsForSearchQuery: List<LocationAutofillSuggestion>,
    weatherOfCurrentUserLocation: BriefWeatherDetails?,
    hourlyForecastsOfCurrentUserLocation: List<HourlyForecast>?,
    isSuggestionsListLoading: Boolean = false,
    isWeatherForSavedLocationsLoading: Boolean = false,
    isCurrentWeatherDetailsLoading: Boolean,
    onSuggestionClick: (LocationAutofillSuggestion) -> Unit,
    onSavedLocationItemClick: (BriefWeatherDetails) -> Unit,
    onSavedLocationDismissed: (BriefWeatherDetails) -> Unit,
    errorFetchingWeatherForCurrentLocation: Boolean,
    errorFetchingWeatherForSavedLocations: Boolean,
    errorLoadingAutofillSuggestions: Boolean,
    onRetryFetchingWeatherForSavedLocations: () -> Unit,
    onRetryFetchingWeatherForCurrentLocation: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onLocationPermissionGranted: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var isSearchBarActive by remember { mutableStateOf(false) }
    var currentQueryText by remember { mutableStateOf("") }
    val clearQueryText = {
        currentQueryText = ""
        onSearchQueryChange("")
    }
    var shouldDisplayCurrentLocationWeatherSubHeader by remember { mutableStateOf(false) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { isPermitted ->
            val isCoarseLocationPermitted =
                isPermitted.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false)
            val isFineLocationPermitted =
                isPermitted.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false)
            if (isCoarseLocationPermitted || isFineLocationPermitted) {
                shouldDisplayCurrentLocationWeatherSubHeader = true
                onLocationPermissionGranted()
            }
        }
    )
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    Box {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = WindowInsets.navigationBars.asPaddingValues()
        ) {
            searchBarItem(
                currentSearchQuery = currentQueryText,
                onClearSearchQueryIconClick = clearQueryText,
                isSearchBarActive = isSearchBarActive,
                errorLoadingSuggestions = errorLoadingAutofillSuggestions,
                onSearchQueryChange = {
                    currentQueryText = it
                    onSearchQueryChange(it)
                },
                onSearchBarActiveChange = { isSearchBarActive = it },
                suggestionsForSearchQuery = suggestionsForSearchQuery,
                isSuggestionsListLoading = isSuggestionsListLoading,
                onSuggestionClick = onSuggestionClick
            )

            if (shouldDisplayCurrentLocationWeatherSubHeader) {
                subHeaderItem(
                    title = "Current Location",
                    isLoadingAnimationVisible = isCurrentWeatherDetailsLoading
                )
            }

            if (weatherOfCurrentUserLocation != null && hourlyForecastsOfCurrentUserLocation != null) {
                currentWeatherDetailCardItem(
                    weatherOfCurrentUserLocation = weatherOfCurrentUserLocation,
                    hourlyForecastsOfCurrentUserLocation = hourlyForecastsOfCurrentUserLocation,
                    onClick = { onSavedLocationItemClick(weatherOfCurrentUserLocation) }
                )
            }

            if (errorFetchingWeatherForCurrentLocation) {
                errorCardItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    errorMessage = "An error occurred when fetching the weather for the current location.",
                    onRetryButtonClick = onRetryFetchingWeatherForCurrentLocation
                )
            }

            subHeaderItem(
                title = "Saved Locations",
                isLoadingAnimationVisible = isWeatherForSavedLocationsLoading
            )

            if (errorFetchingWeatherForSavedLocations) {
                errorCardItem(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    errorMessage = "An error occurred when fetching the current weather details of saved locations.",
                    onRetryButtonClick = onRetryFetchingWeatherForSavedLocations
                )
            }

            savedLocationItems(
                savedLocationItemsList = weatherDetailsOfSavedLocations,
                onSavedLocationItemClick = onSavedLocationItemClick,
                onSavedLocationDismissed = onSavedLocationDismissed
            )
        }
        SnackbarHost(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            hostState = snackbarHostState
        )
    }
}

/**
 * A composable that contains a centered [SearchBar] meant to be used in the lazy column defined in
 * [HomeScreen].
 *
 * Note: In this composable the [SearchBar]'s max height and width are constrained to the max height
 * and width of the screen. Using it in a lazy column using [LazyListScope.item], will cause the app
 * to crash. This is because the width of the [SearchBar], when expanded is set to infinity. A composable
 * of width infinity, in a lazy column, will make the app crash. Hence, the size is explicitly
 * constrained. This might be a bug, and might be fixed in the future.
 */
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
private fun Header(
    modifier: Modifier = Modifier,
    currentSearchQuery: String,
    onClearSearchQueryIconClick: () -> Unit,
    isSearchBarActive: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchBarActiveChange: (Boolean) -> Unit,
    searchBarSuggestionsContent: @Composable (ColumnScope.() -> Unit)
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Column(modifier = modifier) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = currentSearchQuery,
                    onQueryChange = onSearchQueryChange,
                    onSearch = {
                        // No need for this callback because this app uses instant search
                    },
                    expanded = isSearchBarActive,
                    onExpandedChange = onSearchBarActiveChange,
                    placeholder = { Text(text = "Search for a location") },
                    leadingIcon = {
                        AnimatedSearchBarLeadingIcon(
                            isSearchBarActive = isSearchBarActive,
                            onSearchIconClick = { onSearchBarActiveChange(true) },
                            onBackIconClick = {
                                // Clear Search Query text when clicking on the Back Button
                                onClearSearchQueryIconClick()
                                onSearchBarActiveChange(false)
                            }
                        )
                    },
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = isSearchBarActive,
                            enter = slideInHorizontally(initialOffsetX = { it }),
                            exit = slideOutHorizontally(targetOffsetX = { it })
                        ) {
                            val iconImageVector = Icons.Filled.Close
                            IconButton(
                                onClick = onClearSearchQueryIconClick,
                                content = { Icon(imageVector = iconImageVector, contentDescription = null) }
                            )
                        }
                    },
                )
            },
            expanded = isSearchBarActive,
            onExpandedChange = onSearchBarActiveChange,
            modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .sizeIn(
                            maxWidth = screenWidth, // See Docs for Explanation
                            maxHeight = screenHeight // See Docs for Explanation
                        ),
            content = searchBarSuggestionsContent,
        )
    }
}

/**
 * This composable creates an animated search bar leading icon, switching between a back button
 * icon and a search buttin icon based on the [isSearchBarActive] state.
 *
 * @param isSearchBarActive Indicates whether the search bar is active or not.
 * @param onSearchIconClick The callback that will be executed when the search icon is clicked.
 * @param onBackIconClick The callback that will be executed when the back icon is clicked.
 */
@ExperimentalAnimationApi
@Composable
private fun AnimatedSearchBarLeadingIcon(
    isSearchBarActive: Boolean,
    onSearchIconClick: () -> Unit,
    onBackIconClick: () -> Unit
) {
    AnimatedContent(
        targetState = isSearchBarActive,
        transitionSpec = {
            val isActive = this.targetState
            val slideIn = slideIntoContainer(
                if (isActive) AnimatedContentTransitionScope.SlideDirection.Start
                else AnimatedContentTransitionScope.SlideDirection.End
            )
            val slideOut = slideOutOfContainer(
                if (isActive) AnimatedContentTransitionScope.SlideDirection.Start
                else AnimatedContentTransitionScope.SlideDirection.End
            )
            slideIn togetherWith slideOut
        }
    ) { isActive ->
        if (isActive) {
            IconButton(
                onClick = onBackIconClick,
                content = { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) }
            )
        } else {
            IconButton(
                onClick = onSearchIconClick,
                content = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) }
            )
        }
    }
}

@Composable
private fun AutoFillSuggestionsList(
    suggestions: List<LocationAutofillSuggestion>,
    onSuggestionClick: (LocationAutofillSuggestion) -> Unit,
    isSuggestionsListLoading: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isSuggestionsListLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn {
                autofillSuggestionItems(
                    suggestions = suggestions,
                    onSuggestionClick = onSuggestionClick
                )
                item {
                    Spacer(modifier = Modifier.imePadding())
                }
            }
        }
    }
}

private fun LazyListScope.autofillSuggestionItems(
    suggestions: List<LocationAutofillSuggestion>,
    onSuggestionClick: (LocationAutofillSuggestion) -> Unit
) {
    items(items = suggestions, key = { it.idOfLocation }) {
        AutofillSuggestion(
            title = it.nameOfLocation,
            subText = it.addressOfLocation,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onClick = { onSuggestionClick(it) },
            leadingIcon = { AutofillSuggestionLeadingIcon(countryCode = it.countryCode) }
        )
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
private fun LazyListScope.savedLocationItems(
    savedLocationItemsList: List<BriefWeatherDetails>,
    onSavedLocationItemClick: (BriefWeatherDetails) -> Unit,
    onSavedLocationDismissed: (BriefWeatherDetails) -> Unit
) {
    items(
        items = savedLocationItemsList,
        key = { it.nameOfLocation } // Swipe-able cards will be buggy without keys
    ) {
        // The default "rememberDismissState" uses "rememberSaveable" under the hood.
        // This is an issue because the swiped state gets restored when the item is removed
        // and added back to the list.
        // If an item gets removed (after getting swiped) and is added back to the list,
        // the item's state would still be set to "swiped" because the state got saved in
        // savedInstanceState by rememberSaveable.
        val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
            confirmValueChange = { swipeToDismissBoxValue ->
                if (swipeToDismissBoxValue == SwipeToDismissBoxValue.EndToStart) {
                    onSavedLocationDismissed(it)
                    true
                } else {
                    false
                }
            }
        )

        SwipeToDismissCompactWeatherCard(
            modifier = Modifier.padding(horizontal = 16.dp).animateItem(),
            nameOfLocation = it.nameOfLocation,
            shortDescription = it.shortDescription,
            shortDescriptionIcon = it.shortDescriptionIcon,
            weatherInDegrees = it.currentTemperatureRoundedToInt.toString(),
            onClick = { onSavedLocationItemClick(it) },
            swipeToDismissBoxState = swipeToDismissBoxState
        )
    }
}

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
private fun LazyListScope.searchBarItem(
    currentSearchQuery: String,
    isSearchBarActive: Boolean,
    isSuggestionsListLoading: Boolean,
    errorLoadingSuggestions: Boolean,
    suggestionsForSearchQuery: List<LocationAutofillSuggestion>,
    onClearSearchQueryIconClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchBarActiveChange: (Boolean) -> Unit,
    onSuggestionClick: (LocationAutofillSuggestion) -> Unit
) {
    item {
        val searchBarSuggestionsContent = @Composable {
            AutoFillSuggestionsList(
                suggestions = suggestionsForSearchQuery,
                onSuggestionClick = onSuggestionClick,
                isSuggestionsListLoading = isSuggestionsListLoading
            )
        }
        val errorSearchBarSuggestionsContent = @Composable {
            OutlinedCard(modifier = Modifier.padding(16.dp)) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    text = "An error occurred when fetching the suggestions. Please retype to try again."
                )
            }
        }

        Header(
            modifier = Modifier.fillMaxWidth(),
            currentSearchQuery = currentSearchQuery,
            onClearSearchQueryIconClick = onClearSearchQueryIconClick,
            isSearchBarActive = isSearchBarActive,
            onSearchQueryChange = onSearchQueryChange,
            onSearchBarActiveChange = onSearchBarActiveChange,
            searchBarSuggestionsContent = {
                if (errorLoadingSuggestions) errorSearchBarSuggestionsContent()
                else searchBarSuggestionsContent()
            }
        )
    }
}

private fun LazyListScope.subHeaderItem(title: String, isLoadingAnimationVisible: Boolean) {
    item {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .padding(end = 8.dp),
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Normal
            )
            if (isLoadingAnimationVisible) {
                CircularProgressIndicator(
                    modifier = Modifier.size(12.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@ExperimentalFoundationApi
private fun LazyListScope.currentWeatherDetailCardItem(
    weatherOfCurrentUserLocation: BriefWeatherDetails,
    hourlyForecastsOfCurrentUserLocation: List<HourlyForecast>,
    onClick: () -> Unit
) {
    item {
        CompactWeatherCardWithHourlyForecast(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .animateItem(),
            nameOfLocation = weatherOfCurrentUserLocation.nameOfLocation,
            shortDescription = weatherOfCurrentUserLocation.shortDescription,
            shortDescriptionIcon = weatherOfCurrentUserLocation.shortDescriptionIcon,
            weatherInDegrees = weatherOfCurrentUserLocation.currentTemperatureRoundedToInt.toString(),
            onClick = onClick,
            hourlyForecasts = hourlyForecastsOfCurrentUserLocation
        )
    }
}

@Composable
private fun AutofillSuggestionLeadingIcon(countryCode: String) {
    val context = LocalContext.current

    // Build the local asset path
    val assetPath = "file:///android_asset/flags/${countryCode.lowercase()}.svg"

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }

    val imageRequest = remember(assetPath) {
        ImageRequest.Builder(context)
            .data(assetPath)
            .build()
    }

    val painter = rememberAsyncImagePainter(model = imageRequest, imageLoader = imageLoader)

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = "Flag of $countryCode",
            modifier = Modifier.fillMaxSize()
        )
    }
}


@ExperimentalFoundationApi
private fun LazyListScope.errorCardItem(
    errorMessage: String,
    modifier: Modifier = Modifier,
    retryButtonText: String = "Retry",
    onRetryButtonClick: () -> Unit
) {
    item {
        OutlinedCard(modifier = modifier.animateItem()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = errorMessage,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedButton(
                    onClick = onRetryButtonClick,
                    content = { Text(text = retryButtonText) })
            }
        }
    }
}