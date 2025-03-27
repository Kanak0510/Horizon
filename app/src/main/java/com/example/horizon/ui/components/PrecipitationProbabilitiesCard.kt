package com.example.horizon.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.horizon.R
import com.example.horizon.domain.models.PrecipitationProbability
import com.example.horizon.domain.models.probabilityPercentageString

/**
 * A card composable that displays precipitation probabilities in a "progress bar" styled manner.
 * @param precipitationProbabilities The list of precipitation probabilities.
 * @param modifier The modifier to apply to the card.
 */
@Composable
fun PrecipitationProbabilitiesCard(
    precipitationProbabilities: List<PrecipitationProbability>,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_day_rain), // todo change icon
                contentDescription = null
            )
            Text(
                text = "Chance of Rain",
                style = MaterialTheme.typography.titleMedium
            )
        }
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            for (precipitationProbability in precipitationProbabilities) {
                ProbabilityProgressRow(
                    modifier = Modifier.fillMaxWidth(),
                    precipitationProbability = precipitationProbability
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun ProbabilityProgressRow(
    precipitationProbability: PrecipitationProbability,
    modifier: Modifier = Modifier
) {
    var progressValue by remember { mutableStateOf(0f) }
    val animatedProgressValue by animateFloatAsState(targetValue = progressValue)
    LaunchedEffect(precipitationProbability) {
        progressValue = precipitationProbability.probability
    }
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = precipitationProbability.hour.toString(),
            style = MaterialTheme.typography.labelLarge
        )
        LinearProgressIndicator(
        progress = { animatedProgressValue },
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .height(24.dp)
            .weight(1f),
        trackColor = ProgressIndicatorDefaults.linearColor.copy(alpha = 0.5f),
        strokeCap = StrokeCap.Round,
        )
        Text(
            text = precipitationProbability.probabilityPercentageString,
            style = MaterialTheme.typography.labelLarge
        )
    }
}