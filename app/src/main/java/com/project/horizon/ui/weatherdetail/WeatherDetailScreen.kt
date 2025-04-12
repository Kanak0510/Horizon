package com.project.horizon.ui.weatherdetail

import android.os.Build.VERSION.SDK_INT
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.project.horizon.R
import com.project.horizon.domain.models.weather.HourlyForecast
import com.project.horizon.domain.models.weather.PrecipitationProbability
import com.project.horizon.domain.models.weather.SingleWeatherDetail
import com.project.horizon.ui.components.HourlyForecastCard
import com.project.horizon.ui.components.PrecipitationProbabilitiesCard
import com.project.horizon.ui.components.SingleWeatherDetailCard
import com.project.horizon.ui.components.TypingAnimatedText

/**
 * A top-level composable that displays the content of the Weather Detail screen based on the
 * provided [uiState]. Shows either a loading spinner, error state, or the main weather content.
 */
@Composable
fun WeatherDetailScreen(
    uiState: WeatherDetailScreenUiState,
    snackbarHostState: SnackbarHostState,
    onSaveButtonClick: () -> Unit,
    onBackButtonClick: () -> Unit,
) {
    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        uiState.errorMessage != null -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.errorMessage,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = onBackButtonClick) {
                    Text("Go Back")
                }
            }
        }
        else -> {
            WeatherDetailScreen(
                snackbarHostState = snackbarHostState,
                nameOfLocation = uiState.weatherDetailsOfChosenLocation!!.nameOfLocation,
                weatherConditionImage = uiState.weatherDetailsOfChosenLocation.imageResId,
                weatherConditionIconId = uiState.weatherDetailsOfChosenLocation.iconResId,
                weatherInDegrees = uiState.weatherDetailsOfChosenLocation.temperatureRoundedToInt,
                weatherCondition = uiState.weatherDetailsOfChosenLocation.weatherCondition,
                aiGeneratedWeatherSummaryText = uiState.weatherSummaryText,
                isWeatherSummaryLoading = uiState.isWeatherSummaryTextLoading,
                isPreviouslySavedLocation = uiState.isPreviouslySavedLocation,
                singleWeatherDetails = uiState.additionalWeatherInfoItems,
                hourlyForecasts = uiState.hourlyForecasts,
                precipitationProbabilities = uiState.precipitationProbabilities,
                onBackButtonClick = onBackButtonClick,
                onSaveButtonClick = onSaveButtonClick
            )
        }
    }
}

/**
 * The actual Weather Detail screen content that lays out all weather components.
 */
@Composable
fun WeatherDetailScreen(
    nameOfLocation: String,
    @DrawableRes weatherConditionImage: Int,
    @DrawableRes weatherConditionIconId: Int,
    weatherInDegrees: Int,
    weatherCondition: String,
    onBackButtonClick: () -> Unit,
    aiGeneratedWeatherSummaryText: String?,
    isWeatherSummaryLoading: Boolean,
    isPreviouslySavedLocation: Boolean,
    onSaveButtonClick: () -> Unit,
    singleWeatherDetails: List<SingleWeatherDetail>,
    hourlyForecasts: List<HourlyForecast>,
    precipitationProbabilities: List<PrecipitationProbability>,
    snackbarHostState: SnackbarHostState
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Box {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item(span = { GridItemSpan(maxLineSpan) }) {
                Header(
                    modifier = Modifier
                        .requiredWidth(screenWidth)
                        .height(350.dp),
                    headerImageResId = weatherConditionImage,
                    weatherConditionIconId = weatherConditionIconId,
                    onBackButtonClick = onBackButtonClick,
                    shouldDisplaySaveButton = !isPreviouslySavedLocation,
                    onSaveButtonClick = onSaveButtonClick,
                    nameOfLocation = nameOfLocation,
                    currentWeatherInDegrees = weatherInDegrees,
                    weatherCondition = weatherCondition
                )
            }

            // AI Summary
            if (aiGeneratedWeatherSummaryText != null || isWeatherSummaryLoading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    WeatherSummaryTextCard(
                        summaryText = aiGeneratedWeatherSummaryText ?: "",
                        isWeatherSummaryLoading = isWeatherSummaryLoading
                    )
                }
            }

            // Forecast and Precipitation
            item(span = { GridItemSpan(maxLineSpan) }) {
                HourlyForecastCard(hourlyForecasts = hourlyForecasts)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                PrecipitationProbabilitiesCard(precipitationProbabilities = precipitationProbabilities)
            }

            // Additional weather info
            items(singleWeatherDetails) {
                SingleWeatherDetailCard(
                    name = it.name,
                    value = it.value,
                    iconResId = it.iconResId
                )
            }

            // Spacer
            item {
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }

        // Snackbar
        SnackbarHost(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            hostState = snackbarHostState
        )
    }
}

/**
 * Header section with background image, location, and main weather info.
 */
@Composable
private fun Header(
    modifier: Modifier = Modifier,
    @DrawableRes headerImageResId: Int,
    @DrawableRes weatherConditionIconId: Int,
    onBackButtonClick: () -> Unit,
    shouldDisplaySaveButton: Boolean,
    onSaveButtonClick: () -> Unit,
    nameOfLocation: String,
    currentWeatherInDegrees: Int,
    weatherCondition: String
) {
    Box(modifier = modifier) {
        val iconBgColor = remember { Color.Black.copy(alpha = 0.4f) }

        Image(
            painter = painterResource(id = headerImageResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.3f))) // Image scrim

        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding(),
            onClick = onBackButtonClick,
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = iconBgColor)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
        }

        if (shouldDisplaySaveButton) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding(),
                onClick = onSaveButtonClick,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = iconBgColor)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = nameOfLocation,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
            Text(
                text = "$currentWeatherInDegrees°",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 80.sp)
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.offset(x = (-8).dp)) {
                Icon(
                    imageVector = ImageVector.vectorResource(weatherConditionIconId),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(32.dp)
                )
                Text(text = weatherCondition)
            }
        }
    }
}

/**
 * Card that displays the AI-generated summary for current weather conditions.
 */
@Composable
private fun WeatherSummaryTextCard(
    modifier: Modifier = Modifier,
    isWeatherSummaryLoading: Boolean,
    summaryText: String
) {
    Card(modifier = modifier) {
        val context = LocalContext.current

        val imageLoader = remember {
            ImageLoader.Builder(context).components {
                if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory()) else add(GifDecoder.Factory())
            }.build()
        }

        val imageRequest = remember {
            ImageRequest.Builder(context)
                .data(R.drawable.bard_sparkle_thinking_anim)
                .size(Size.ORIGINAL)
                .build()
        }

        val aiIconPainter = rememberAsyncImagePainter(model = imageRequest, imageLoader = imageLoader)

        Row(
            modifier = Modifier.padding(top = 8.dp, start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isWeatherSummaryLoading) {
                Image(painter = aiIconPainter, contentDescription = null, modifier = Modifier.size(16.dp))
            } else {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_bard_logo),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text("Summary", style = MaterialTheme.typography.titleMedium)
        }

        TypingAnimatedText(
            text = summaryText,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
