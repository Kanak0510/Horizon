package com.project.horizon.data.local.weather

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a weather location saved by the user.
 * This entity is stored in the "SavedWeatherLocations" table in the Room database.
 *
 * @property nameOfLocation A unique name identifying the location (e.g., "New York"). Acts as the primary key.
 * @property latitude The latitude coordinate of the location (as a String).
 * @property longitude The longitude coordinate of the location (as a String).
 * @property isDeleted A soft-delete flag. If true, the location is considered deleted but remains in the database.
 */
@Entity(tableName = "SavedWeatherLocations")
data class SavedWeatherLocationEntity(
    @PrimaryKey val nameOfLocation: String, // Unique identifier for the saved location
    val latitude: String,                   // Latitude of the location
    val longitude: String,                  // Longitude of the location
    val isDeleted: Boolean = false          // Soft delete flag (default is false)
)
