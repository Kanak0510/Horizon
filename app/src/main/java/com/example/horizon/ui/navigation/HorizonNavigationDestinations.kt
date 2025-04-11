package com.example.horizon.ui.navigation

import com.example.horizon.ui.navigation.HorizonNavigationDestinations.WeatherDetailScreen.NAV_ARG_LATITUDE
import com.example.horizon.ui.navigation.HorizonNavigationDestinations.WeatherDetailScreen.NAV_ARG_LONGITUDE
import com.example.horizon.ui.navigation.HorizonNavigationDestinations.WeatherDetailScreen.NAV_ARG_NAME_OF_LOCATION


/**
 * Represents all navigation destinations in the Horizon app.
 *
 * This sealed class defines the route structure and argument handling for each screen
 * in the application.
 *
 * @property route The route string used by the [NavHost] to navigate to the destination.
 */
sealed class HorizonNavigationDestinations(val route: String) {

    /**
     * Represents the Home Screen destination.
     * This is the main entry point of the application.
     */
    object HomeScreen : HorizonNavigationDestinations(route = "home_screen")

    /**
     * Represents the Weather Detail Screen destination.
     * This screen displays detailed weather information for a specific location.
     *
     * The screen expects the following arguments in the route:
     * - [NAV_ARG_NAME_OF_LOCATION]: Name of the location.
     * - [NAV_ARG_LATITUDE]: Latitude of the location.
     * - [NAV_ARG_LONGITUDE]: Longitude of the location.
     */
    object WeatherDetailScreen : HorizonNavigationDestinations(
        route = "weather_detail/{nameOfLocation}/{latitude}/{longitude}"
    ) {
        /** Navigation argument key for the location name. */
        const val NAV_ARG_NAME_OF_LOCATION = "nameOfLocation"

        /** Navigation argument key for the latitude. */
        const val NAV_ARG_LATITUDE = "latitude"

        /** Navigation argument key for the longitude. */
        const val NAV_ARG_LONGITUDE = "longitude"

        /**
         * Builds the navigation route for the Weather Detail screen with provided parameters.
         *
         * @param nameOfLocation The name of the location.
         * @param latitude The latitude coordinate of the location.
         * @param longitude The longitude coordinate of the location.
         * @return A route string that can be used with [NavController.navigate] to go to the detail screen.
         */
        fun buildRoute(
            nameOfLocation: String,
            latitude: String,
            longitude: String
        ): String = "weather_detail/$nameOfLocation/$latitude/$longitude"
    }
}
