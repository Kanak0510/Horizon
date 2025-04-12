package com.project.horizon.domain.models.weather

import androidx.annotation.DrawableRes
import com.project.horizon.data.local.weather.SavedWeatherLocationEntity
import com.project.horizon.domain.models.location.Coordinates

/**
 * Represents a concise summary of the weather at a given location.
 *
 * @property nameOfLocation The name of the location.
 * @property currentTemperatureRoundedToInt The current temperature in Celsius, rounded to the nearest integer.
 * @property shortDescription A brief textual description of the current weather (e.g., "Sunny").
 * @property shortDescriptionIcon A drawable resource representing the weather condition.
 * @property coordinates The [Coordinates] of the location.
 */
data class BriefWeatherDetails(
    val nameOfLocation: String,
    val currentTemperatureRoundedToInt: Int,
    val shortDescription: String,
    @DrawableRes val shortDescriptionIcon: Int,
    val coordinates: Coordinates
)

/**
 * Maps a [BriefWeatherDetails] instance to a [SavedWeatherLocationEntity] for database storage.
 *
 * @return A [SavedWeatherLocationEntity] containing the location name and coordinates.
 */
fun BriefWeatherDetails.toSavedWeatherLocationEntity(): SavedWeatherLocationEntity =
    SavedWeatherLocationEntity(
        nameOfLocation = nameOfLocation,
        latitude = coordinates.latitude,
        longitude = coordinates.longitude
    )
