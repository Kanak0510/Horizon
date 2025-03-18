package com.example.horizon.ui.components

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.horizon.R
import com.example.horizon.domain.models.HourlyForecast

/**
 * A Card composable that contains a horizontally scrolling list of [hourlyForecasts].
 * @param hourlyForecasts list of hourly forecasts
 * @param modifier modifier for the card
 */
@Composable
fun HourlyForecastCard(
    hourlyForecasts: List<HourlyForecast>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_schedule_24),
                contentDescription = null
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
            items(hourlyForecasts) {
                HourlyForecastItem(
                    hourOfDay = it.hour,
                    isAM = it.isAM,
                    iconResId = it.weatherIconResId,
                    temperatureString = it.temperatureString
                )
            }
        }

    }
}

@Composable
private fun HourlyForecastItem(
    modifier: Modifier = Modifier,
    hourOfDay: Int,
    isAM: Boolean,
    @DrawableRes iconResId: Int,
    temperatureString: String
) {
    val labelLargeStyle = MaterialTheme.typography.labelLarge.toSpanStyle()
    val labelSmallStyle = MaterialTheme.typography.labelSmall.toSpanStyle()
    val hourOfDayText = remember(hourOfDay) {
        buildAnnotatedString {
            withStyle(labelLargeStyle) {
                append(hourOfDay.toString())
            }
            withStyle(labelSmallStyle) {
                append(if (isAM) "AM" else "PM")
            }
        }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = hourOfDayText,
            style = MaterialTheme.typography.labelLarge
        )
        Icon(
            modifier = Modifier.size(40.dp),
            imageVector = ImageVector.vectorResource(id = iconResId),
            contentDescription = null
        )
        Text(
            text = "${temperatureString}°",
            style = MaterialTheme.typography.labelLarge
        )
    }

}