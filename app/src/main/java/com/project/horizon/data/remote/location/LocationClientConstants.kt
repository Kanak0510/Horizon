package com.project.horizon.data.remote.location

/**
 * Contains constants used by the [LocationClient] for making network requests
 * to the location services API.
 */
object LocationClientConstants {

    /**
     * The base URL for the Open-Meteo Geocoding API.
     */
    const val BASE_URL = "https://geocoding-api.open-meteo.com/v1/"

    /**
     * Defines the endpoints available in the location API.
     */
    object EndPoints {
        /**
         * The endpoint path for retrieving place suggestions based on a query string.
         */
        const val GET_PLACES_SUGGESTIONS_FOR_QUERY = "search"
    }
}
