package com.example.horizon.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.horizon.R
import com.example.horizon.domain.hourStringInTwelveHourFormat
import com.example.horizon.domain.models.weather.PrecipitationProbability

/**
 * Displays a card showing precipitation probabilities using vertical progress indicators.
 *
 * @param precipitationProbabilities List of [PrecipitationProbability] data.
 * @param modifier Modifier to apply to the card.
 */
@Composable
fun PrecipitationProbabilitiesCard(
    precipitationProbabilities: List<PrecipitationProbability>,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_chance_of_rain),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = "Chance of Rain",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(precipitationProbabilities) { probability ->
                    ProbabilityProgressColumn(
                        precipitationProbability = probability,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

/**
 * A single vertical progress bar with time and percentage for precipitation probability.
 *
 * @param precipitationProbability The data representing one forecasted precipitation value.
 * @param modifier Modifier for layout adjustments.
 */
@Composable
private fun ProbabilityProgressColumn(
    precipitationProbability: PrecipitationProbability,
    modifier: Modifier = Modifier
) {
    var progressValue by remember { mutableStateOf(0f) }
    val animatedProgressValue by animateFloatAsState(targetValue = progressValue)

    LaunchedEffect(precipitationProbability) {
        progressValue = precipitationProbability.probabilityPercentage / 100f
    }

    val height = 120.dp
    val width = 16.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = precipitationProbability.dateTime.hourStringInTwelveHourFormat.padStart(5),
            style = MaterialTheme.typography.labelLarge
        )

        Box(
            modifier = Modifier.size(height = height, width = width)
        ) {
            LinearProgressIndicator(
                progress = { animatedProgressValue },
                modifier = Modifier
                    .align(Alignment.Center)
                    .requiredSize(
                        height = width,
                        width = height
                    )
                    .rotate(-90f),
                trackColor = ProgressIndicatorDefaults.linearColor.copy(alpha = 0.5f),
                strokeCap = StrokeCap.Round
            )
        }

        Text(
            text = "${precipitationProbability.probabilityPercentage}%" .padStart(4),
            style = MaterialTheme.typography.labelLarge
        )
    }
}
