package com.example.horizon.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.horizon.domain.models.BriefWeatherDetails
import com.example.horizon.domain.models.LocationAutofillSuggestion
import com.example.horizon.ui.components.AutofillSuggestion
import com.example.horizon.ui.components.SwipeToDismissCompactWeatherCard

/**
 * A home screen composable that displays a search bar with a list containing the current weather for
 * saved locations.
 *
 * @param modifier The modifier to be applied to the composable.
 * @param weatherDetailsOfSavedLocations The list of weather details of saved locations.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    weatherDetailsOfSavedLocations: List<BriefWeatherDetails>,
    suggestionsForSearchQuery: List<LocationAutofillSuggestion>,
    isSuggestionsListLoading: Boolean = false,
    isWeatherForSavedLocationsLoading: Boolean = false,
    onSuggestionClick: (LocationAutofillSuggestion) -> Unit,
    onSavedLocationItemClick: (BriefWeatherDetails) -> Unit,
    onSavedLocationDismissed: (BriefWeatherDetails) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var isSearchBarActive by remember { mutableStateOf(false) }
    var currentQueryText by remember { mutableStateOf("") }
    val clearQueryText = {
        currentQueryText = ""
        onSearchQueryChange("")
    }

    Box {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Header(
                    modifier = Modifier.fillMaxWidth(),
                    currentSearchQuery = currentQueryText,
                    onClearSearchQueryIconClick = clearQueryText,
                    isSearchBarActive = isSearchBarActive,
                    onSearchQueryChange = {
                        currentQueryText = it
                        onSearchQueryChange(it)
                    },
                    onSearchBarActiveChange = { isSearchBarActive = it },
                    onSearch = {/* TODO: handle search */ },
                    searchBarSuggestionsContent = {
                        AutoFillSuggestionsList(
                            suggestions = suggestionsForSearchQuery,
                            onSuggestionClick = onSuggestionClick,
                            isSuggestionsListLoading = isSuggestionsListLoading
                        )
                    }
                )
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .padding(end = 8.dp),
                        text = "Saved Locations",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Normal
                    )
                    if (isWeatherForSavedLocationsLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            items(
                items = weatherDetailsOfSavedLocations,
                key = { it.nameOfLocation } // Swipe-able cards will be buggy without keys
            ) {
                SwipeToDismissCompactWeatherCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    nameOfLocation = it.nameOfLocation,
                    shortDescription = it.shortDescription,
                    shortDescriptionIcon = it.shortDescriptionIcon,
                    weatherInDegrees = it.currentTemperature,
                    onClick = { onSavedLocationItemClick(it) },
                    swipeToDismissBoxState = { onSavedLocationDismissed(it) }
                )
            }
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
    onSearch: (String) -> Unit,
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
                    onSearch = onSearch,
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
            slideIn.togetherWith(slideOut)
        }
    ) { isActive ->
        if (isActive) {
            IconButton(
                onClick = onBackIconClick,
                content = { Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null) }
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
            onClick = { onSuggestionClick(it) }
        )
    }
}
