package com.project.horizon.domain.models.location

import com.project.horizon.data.local.weather.SavedWeatherLocationEntity

/**
 * A domain model that represents a location saved by the user.
 *
 * @property nameOfLocation The display name of the saved location.
 * @property coordinates The [Coordinates] representing the latitude and longitude of the location.
 */
data class SavedLocation(
    val nameOfLocation: String,
    val coordinates: Coordinates
)

/**
 * Maps a [SavedWeatherLocationEntity] from the local database to the [SavedLocation] domain model.
 *
 * @return A [SavedLocation] object with the mapped name and coordinates.
 */
fun SavedWeatherLocationEntity.toSavedLocation(): SavedLocation = SavedLocation(
    nameOfLocation = nameOfLocation,
    coordinates = Coordinates(
        latitude = latitude,
        longitude = longitude
    )
)
