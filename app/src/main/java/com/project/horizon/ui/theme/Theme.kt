package com.project.horizon.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * A manually defined dark color scheme for the Horizon app.
 * This scheme includes primary, secondary, tertiary, error, surface, and background colors,
 * along with their corresponding on-color and container variants.
 */
private val HorizonDarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue20,
    primaryContainer = Blue30,
    onPrimaryContainer = Blue90,
    inversePrimary = Blue40,

    secondary = DarkBlue80,
    onSecondary = DarkBlue20,
    secondaryContainer = DarkBlue30,
    onSecondaryContainer = DarkBlue90,

    tertiary = Yellow80,
    onTertiary = Yellow20,
    tertiaryContainer = Yellow30,
    onTertiaryContainer = Yellow90,

    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,

    background = Grey10,
    onBackground = Grey90,
    surface = Grey10,
    onSurface = Grey80,
    inverseSurface = Grey90,
    inverseOnSurface = Grey20,

    surfaceVariant = BlueGrey30,
    onSurfaceVariant = BlueGrey80,
    outline = BlueGrey60
)

/**
 * The theme entry point for the Horizon app.
 *
 * This composable applies either a dynamic or manually defined dark color scheme, depending
 * on the device's OS version and the [areDynamicColorsEnabled] flag.
 *
 * @param areDynamicColorsEnabled If true, attempts to apply dynamic color theming on supported devices (Android 12+).
 * @param content The composable UI content to be styled with the Horizon theme.
 */
@Composable
fun HorizonTheme(
    areDynamicColorsEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val supportsDynamicColors = areDynamicColorsEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    MaterialTheme(
        colorScheme = if (supportsDynamicColors) {
            dynamicDarkColorScheme(context)
        } else {
            HorizonDarkColorScheme
        },
        typography = Typography,
        content = content
    )
}
