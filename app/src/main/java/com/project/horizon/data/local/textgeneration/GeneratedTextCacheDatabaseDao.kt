package com.project.horizon.data.local.textgeneration

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

/**
 * DAO (Data Access Object) interface for accessing and modifying
 * the local Room database table: GeneratedTextForLocationEntities.
 * This DAO is used for caching generated text based on location-specific weather data.
 */
@Dao
interface GeneratedTextCacheDatabaseDao {

    /**
     * Inserts or updates the generated text entity in the database.
     * If the record already exists (based on primary key), it will be updated.
     *
     * @param generatedTextForLocationEntity The entity to be inserted or updated.
     */
    @Upsert
    suspend fun addGeneratedTextForLocation(generatedTextForLocationEntity: GeneratedTextForLocationEntity)

    /**
     * Retrieves a cached generated text entry from the database
     * that exactly matches the provided location name, temperature,
     * and concise weather description.
     *
     * @param nameOfLocation The name of the location.
     * @param temperature The temperature value.
     * @param conciseWeatherDescription A brief description of the weather.
     * @return The matching GeneratedTextForLocationEntity, or null if not found.
     */
    @Query(
        """
        SELECT * 
        FROM GeneratedTextForLocationEntities
        WHERE nameOfLocation = :nameOfLocation AND
              temperature = :temperature AND
              conciseWeatherDescription = :conciseWeatherDescription
        """
    )
    suspend fun getSavedGeneratedTextForDetails(
        nameOfLocation: String,
        temperature: Int,
        conciseWeatherDescription: String
    ): GeneratedTextForLocationEntity?

    /**
     * Deletes all entries from the generated text cache table.
     * Useful for clearing old or unused cached data.
     */
    @Query("DELETE from GeneratedTextForLocationEntities")
    suspend fun deleteAllSavedText()
}
