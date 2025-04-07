package com.example.horizon.domain.models.location

import com.example.horizon.data.local.weather.SavedWeatherLocationEntity

/**
 * A domain object that represents a location saved by the user.
 */
data class SavedLocation(
    val nameOfLocation: String,
    val coordinates: Coordinates
)

/**
 * Used to map an instance of [SavedWeatherLocationEntity] to an instance of [SavedLocation].
 */
fun SavedWeatherLocationEntity.toSavedLocation() = SavedLocation(
    nameOfLocation = nameOfLocation,
    coordinates = Coordinates(latitude = latitude, longitude = longitude)
)
