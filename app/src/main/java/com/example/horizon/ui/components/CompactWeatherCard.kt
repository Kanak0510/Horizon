package com.example.horizon.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
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

/**
 * A compact weather card UI that shows the location, a brief description, and temperature.
 *
 * @param nameOfLocation The name of the location (e.g., "New York").
 * @param shortDescription A short weather description (e.g., "Sunny").
 * @param shortDescriptionIcon Drawable resource representing the weather condition.
 * @param weatherInDegrees Temperature value in string format (e.g., "27").
 * @param onClick Callback invoked when the card is clicked.
 * @param modifier Modifier to apply to the entire card.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactWeatherCard(
    nameOfLocation: String,
    shortDescription: String,
    @DrawableRes shortDescriptionIcon: Int,
    weatherInDegrees: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val weatherWithDegreeSymbol = remember(weatherInDegrees) {
        // Use a visually appropriate degree symbol (°)
        "$weatherInDegrees°"
    }

    OutlinedCard(
        modifier = modifier,
        onClick = onClick
    ) {
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
                text = weatherWithDegreeSymbol,
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}

/**
 * Displays the weather description alongside an icon.
 *
 * @param shortDescription Weather condition description.
 * @param iconRes Drawable resource for the icon.
 * @param modifier Modifier to apply to the row.
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
            tint = Color.Unspecified // Ensures the original icon color is preserved
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

/**
 * A [CompactWeatherCard] wrapped with swipe-to-dismiss behavior.
 *
 * @param swipeToDismissBoxState State used to control the swipe gesture.
 * @param nameOfLocation Location name for the card.
 * @param shortDescription Description of the weather.
 * @param shortDescriptionIcon Icon for the weather.
 * @param weatherInDegrees Temperature in degrees (as a string).
 * @param onClick Callback when the card is tapped.
 * @param modifier Modifier to apply to the swipe box.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissCompactWeatherCard(
    nameOfLocation: String,
    shortDescription: String,
    @DrawableRes shortDescriptionIcon: Int,
    weatherInDegrees: String,
    onClick: () -> Unit,
    swipeToDismissBoxState: SwipeToDismissBoxState,
    modifier: Modifier = Modifier
) {
    SwipeToDismissBox(
        modifier = modifier,
        state = swipeToDismissBoxState,
        backgroundContent = {},
        enableDismissFromStartToEnd = false,
        content = {
            CompactWeatherCard(
                nameOfLocation = nameOfLocation,
                shortDescription = shortDescription,
                shortDescriptionIcon = shortDescriptionIcon,
                weatherInDegrees = weatherInDegrees,
                onClick = onClick
            )
        }
    )
}
