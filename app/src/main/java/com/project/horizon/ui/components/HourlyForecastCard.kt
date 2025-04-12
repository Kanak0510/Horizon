package com.project.horizon.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.project.horizon.R
import com.project.horizon.domain.hourStringInTwelveHourFormat
import com.project.horizon.domain.models.weather.HourlyForecast
import java.time.LocalDateTime

/**
 * Displays a horizontally scrollable card of hourly weather forecasts.
 *
 * @param hourlyForecasts List of [HourlyForecast] items to display.
 * @param modifier Modifier to customize layout of the card.
 */
@Composable
fun HourlyForecastCard(
    hourlyForecasts: List<HourlyForecast>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_schedule_24),
                    contentDescription = null,
                    tint = Color.Unspecified // preserve original color
                )
                Text(
                    text = "Hourly Forecast",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
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
 * A single item in the hourly forecast list.
 *
 * @param dateTime Time for the forecasted weather.
 * @param iconResId Drawable resource for the forecast weather icon.
 * @param temperature Temperature value to display.
 * @param modifier Modifier to customize layout.
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
            tint = Color.Unspecified
        )

        Text(
            text = "$temperature°",
            style = MaterialTheme.typography.labelLarge
        )
    }
}
