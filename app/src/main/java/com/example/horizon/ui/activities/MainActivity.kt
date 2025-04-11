package com.example.horizon.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.example.horizon.ui.navigation.HorizonNavigation
import com.example.horizon.ui.theme.HorizonTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The entry point of the Horizon app. This activity sets up the splash screen,
 * applies full-screen system window flags, and loads the main composable content.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Installs the splash screen before any other setup
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Allows the content to draw behind system bars (e.g., status and navigation bars)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Sets the Jetpack Compose content
        setContent {
            HorizonTheme {
                Surface {
                    HorizonNavigation()
                }
            }
        }
    }
}
