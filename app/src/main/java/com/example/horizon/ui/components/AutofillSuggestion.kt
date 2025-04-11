package com.example.horizon.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * A composable used to show location autofill suggestions.
 *
 * @param title The main text, typically a location or place name.
 * @param subText A secondary line, such as an address or region.
 * @param onClick Callback invoked when the suggestion is selected.
 * @param modifier Modifier to apply to the suggestion row.
 * @param leadingIcon Optional leading icon composable. Defaults to a styled location icon.
 */
@Composable
fun AutofillSuggestion(
    title: String,
    subText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable () -> Unit = { DefaultLeadingIcon() }
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon()

        Spacer(modifier = Modifier.size(16.dp))

        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = subText,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Normal,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

/**
 * Default location icon used for autofill suggestions.
 * Draws a soft white background circle behind the icon for visual enhancement.
 */
@Composable
private fun DefaultLeadingIcon() {
    Icon(
        modifier = Modifier
            .size(40.dp)
            .drawBehind {
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    center = center,
                    radius = size.minDimension / 1.7f
                )
            },
        imageVector = Icons.Filled.LocationOn,
        tint = Color.White,
        contentDescription = null
    )
}
