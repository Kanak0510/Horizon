package com.project.horizon.data.local.textgeneration

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a cached text entity used to store a generated weather description
 * for a specific location with given weather conditions.
 *
 * This entity is used in Room database under the table name "GeneratedTextForLocationEntities".
 *
 * @property nameOfLocation The name of the location. Serves as the primary key.
 * @property temperature The temperature value associated with the generated description.
 * @property conciseWeatherDescription A short description of the current weather (e.g., "Cloudy", "Sunny").
 * @property generatedDescription The actual generated text/description for the specified weather conditions.
 */
@Entity(tableName = "GeneratedTextForLocationEntities")
data class GeneratedTextForLocationEntity(
    @PrimaryKey val nameOfLocation: String, // Unique identifier for the location (primary key)
    val temperature: Int,                   // Weather temperature for the location
    val conciseWeatherDescription: String, // Brief description of the weather
    val generatedDescription: String,      // Generated text based on the above conditions
)
