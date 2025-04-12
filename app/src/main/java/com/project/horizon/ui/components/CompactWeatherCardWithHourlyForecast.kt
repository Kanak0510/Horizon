package com.project.horizon.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.project.horizon.domain.hourStringInTwelveHourFormat
import com.project.horizon.domain.models.weather.HourlyForecast
import java.time.LocalDateTime

/**
 * A weather card that shows basic weather info along with a horizontally scrollable
 * list of hourly forecasts.
 *
 * @param nameOfLocation Location name (e.g., "San Francisco").
 * @param shortDescription Brief description of the current weather.
 * @param shortDescriptionIcon Drawable resource for the current weather icon.
 * @param weatherInDegrees Temperature in degrees (e.g., "25").
 * @param onClick Callback invoked when the card is clicked.
 * @param hourlyForecasts List of [HourlyForecast] data to be displayed in a horizontal list.
 * @param modifier Modifier for the card container.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactWeatherCardWithHourlyForecast(
    nameOfLocation: String,
    shortDescription: String,
    @DrawableRes shortDescriptionIcon: Int,
    weatherInDegrees: String,
    onClick: () -> Unit,
    hourlyForecasts: List<HourlyForecast>,
    modifier: Modifier = Modifier
) {
    val temperatureWithDegree = remember(weatherInDegrees) { "$weatherInDegrees°" }

    OutlinedCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = nameOfLocation,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )
                    ShortWeatherDescriptionWithIconRow(
                        shortDescription = shortDescription,
                        iconRes = shortDescriptionIcon
                    )
                }

                Text(
                    text = temperatureWithDegree,
                    style = MaterialTheme.typography.displayMedium
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(hourlyForecasts) { forecast ->
                    HourlyForecastItem(
                        dateTime = forecast.dateTime,
                        iconResId = forecast.weatherIconResId,
                        temperature = forecast.temperature
                    )
                }
            }
        }
    }
}

/**
 * Represents a single forecast item in the horizontal scroll.
 *
 * @param dateTime Time for the forecast (used for hour label).
 * @param iconResId Icon representing the weather condition.
 * @param temperature Temperature at the given hour.
 * @param modifier Modifier for layout customization.
 */
@Composable
private fun HourlyForecastItem(
    dateTime: LocalDateTime,
    @DrawableRes iconResId: Int,
    temperature: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dateTime.hourStringInTwelveHourFormat,
            style = MaterialTheme.typography.labelLarge
        )

        Icon(
            modifier = Modifier.size(40.dp),
            imageVector = ImageVector.vectorResource(id = iconResId),
            contentDescription = null,
            tint = Color.Unspecified // preserves original vector color
        )

        Text(
            text = "$temperature°",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Displays a weather description and an icon in a horizontal row.
 *
 * @param shortDescription Short description of the weather.
 * @param iconRes Icon to represent the description.
 * @param modifier Modifier for the row.
 */
@Composable
private fun ShortWeatherDescriptionWithIconRow(
    shortDescription: String,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = shortDescription,
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Normal
        )
    }
}
