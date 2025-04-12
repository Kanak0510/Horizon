package com.project.horizon.data.remote.location.models

import kotlinx.serialization.Serializable

/**
 * Represents the API response containing geographical coordinate data for a given location.
 *
 * @property features A list of features, each containing geometry data with coordinates.
 */
@Serializable
data class CoordinatesResponse(val features: List<Feature>) {

    /**
     * Represents a single feature within the [CoordinatesResponse], which contains
     * geometric data related to the location.
     *
     * @property geometry An object that holds the coordinates of this feature.
     */
    @Serializable
    data class Feature(val geometry: Geometry) {

        /**
         * Represents the geometry section of a feature, containing a list of
         * coordinates [longitude, latitude] as strings.
         *
         * @property coordinates A list containing the longitude and latitude of the feature.
         */
        @Serializable
        data class Geometry(val coordinates: List<String>)
    }

    /**
     * A simplified model to hold the parsed coordinates of a location.
     *
     * @property longitude The longitude value as a string.
     * @property latitude The latitude value as a string.
     */
    data class Coordinates(val longitude: String, val latitude: String)
}

/**
 * Extension property to retrieve the parsed [CoordinatesResponse.Coordinates] from the
 * first feature of the [CoordinatesResponse].
 *
 * @return A [CoordinatesResponse.Coordinates] object containing the longitude and latitude.
 * @throws NoSuchElementException if the features list is empty or the coordinates are missing.
 */
val CoordinatesResponse.coordinates: CoordinatesResponse.Coordinates
    get() {
        val (longitude, latitude) = features.first().geometry.coordinates
        return CoordinatesResponse.Coordinates(longitude, latitude)
    }
