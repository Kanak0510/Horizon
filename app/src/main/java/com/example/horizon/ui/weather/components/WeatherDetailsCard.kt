package com.example.horizon.ui.weather.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.horizon.R
import com.example.horizon.domain.models.WeatherDetails

/**
 * A data class that contains all weather icons used in [WeatherDetailsCard].
 *
 * @param minTempIcon The minimum temperature icon.
 * @param maxTempIcon The maximum temperature icon.
 * @param humidityIcon The humidity icon.
 * @param windSpeedIcon The wind speed icon.
 * @param windDirectionIcon The wind direction icon.
 * @param pressureIcon The pressure icon.
 */
data class WeatherDetailIcons(
    @DrawableRes val minTempIcon: Int = R.drawable.ic_min_temp,
    @DrawableRes val maxTempIcon: Int = R.drawable.ic_max_temp,
    @DrawableRes val humidityIcon: Int = R.drawable.ic_humidity,
    @DrawableRes val windSpeedIcon: Int = R.drawable.ic_wind_speed,
    @DrawableRes val windDirectionIcon: Int = R.drawable.ic_wind_direction,
    @DrawableRes val pressureIcon: Int = R.drawable.ic_wind_pressure
)

/**
 * A Weather details card composable that displays the [weatherDetails] with the appropriate
 * icons, with the provided [weatherDetailsIcons].
 * @param modifier the modifier to be applied to the composable
 * @param cardColors  [CardColors] that will be used to resolve the colors used for this card in
 * different states. See [CardDefaults.cardColors].
 */
@ExperimentalLayoutApi
@Composable
fun WeatherDetailsCard(
    modifier: Modifier = Modifier,
    cardColors: CardColors = CardDefaults.cardColors(),
    weatherDetails: WeatherDetails,
    weatherDetailsIcons: WeatherDetailIcons = WeatherDetailIcons(),
    shortWeatherDescription: String
) {
    Card(
        modifier = Modifier
            .navigationBarsPadding()
            .then(modifier)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            text = shortWeatherDescription,
            maxLines = 2,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CardItem(
                title = "Low",
                value = weatherDetails.temperature.minTemperature,
                imageVector = ImageVector.vectorResource(weatherDetailsIcons.minTempIcon)
            )
            CardItem(
                title = "High",
                value = weatherDetails.temperature.maxTemperature,
                imageVector = ImageVector.vectorResource(weatherDetailsIcons.maxTempIcon)
            )
            CardItem(
                title = "Humidity",
                value = weatherDetails.humidity,
                imageVector = ImageVector.vectorResource(weatherDetailsIcons.humidityIcon)
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CardItem(
                title = "Wind Speed",
                value = weatherDetails.wind.speed,
                imageVector = ImageVector.vectorResource(weatherDetailsIcons.windSpeedIcon)
            )
            CardItem(
                title = "Wind Direction",
                value = weatherDetails.wind.direction,
                imageVector = ImageVector.vectorResource(weatherDetailsIcons.windDirectionIcon)
            )
            CardItem(
                title = "Pressure",
                value = weatherDetails.humidity,
                imageVector = ImageVector.vectorResource(weatherDetailsIcons.pressureIcon)
            )
        }
    }
}

/**
 * A composable that represents one item in [WeatherDetailsCard].
 */
@Composable
private fun CardItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    imageVector: ImageVector
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = imageVector,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // applied emphasis according to material design spec
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f), // applied emphasis according to material design spec
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Medium
        )
    }
}
