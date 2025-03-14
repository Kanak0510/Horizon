package com.example.horizon.ui.navigation

/**
 * A sealed class that contains all the destinations of the app.
 */
sealed class HorizonNavigationDestinations(val route: String) {

    object HomeScreen : HorizonNavigationDestinations(route = "home_screen")

    object WeatherDetailScreen :
        HorizonNavigationDestinations(route = "weather_detail/{latitude}/{longitude}") {
        const val NAV_ARG_LATITUDE = "latitude"
        const val NAV_ARG_LONGITUDE = "longitude"

        /**
         * Builds the route string for the weather detail screen destination with the provided
         * [latitude] and [longitude].
         */
        fun buildRoute(latitude: String, longitude: String) = "weather_detail/$latitude/$longitude"
    }
}