package com.example.horizon.ui.weatherDetail

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.horizon.domain.models.HourlyForecast
import com.example.horizon.domain.models.PrecipitationProbability
import com.example.horizon.domain.models.SingleWeatherDetail
import com.example.horizon.ui.components.HourlyForecastCard
import com.example.horizon.ui.components.PrecipitationProbabilitiesCard
import com.example.horizon.ui.components.SingleWeatherDetailCard

// todo stopship - need to add top bar when screen is fully scrolled + add Save button
@Composable
fun WeatherDetailScreen(
    nameOfLocation: String,
    @DrawableRes weatherConditionImage: Int,
    @DrawableRes weatherConditionIconId: Int,
    weatherInDegrees: Int,
    weatherCondition: String,
    onBackButtonClick: () -> Unit,
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
            columns = GridCells.Fixed(count = 2),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Header(
                    modifier = Modifier
                        .requiredWidth(screenWidth)
                        .height(360.dp),
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

            item(span = { GridItemSpan(maxLineSpan) }) {
                HourlyForecastCard(hourlyForecasts = hourlyForecasts)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                PrecipitationProbabilitiesCard(precipitationProbabilities = precipitationProbabilities)
            }
            items(singleWeatherDetails) {
                SingleWeatherDetailCard(
                    name = it.name,
                    value = it.value,
                    iconResId = it.iconResId
                )
            }
            item {
                Spacer(modifier = Modifier.navigationBarsPadding())
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
    weatherCondition: String,
) {
    Box(modifier = modifier) {
        val iconButtonContainerColor = remember {
            Color.Black.copy(0.4f)
        }
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = headerImageResId),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        // Scrim for image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
        )

        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding(),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = iconButtonContainerColor
            ),
            onClick = onBackButtonClick
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null
            )
        }
        if (shouldDisplaySaveButton) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = iconButtonContainerColor
                ),
                onClick = onSaveButtonClick
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                text = nameOfLocation,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$currentWeatherInDegrees°",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 80.sp)
            )
            Row(
                modifier = Modifier.offset(x = (-8).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Explicitly set tint to Color.Unspecified to ensure that no tint is applied to the vector
                // resource. See documentation of the Icon composable for more information.
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = ImageVector.vectorResource(id = weatherConditionIconId),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                Text(text = weatherCondition)
            }
        }
    }
}