package com.project.horizon.data.local.weather

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) for performing CRUD operations on the
 * [SavedWeatherLocationEntity] table within the Room database.
 *
 * This interface provides functions to fetch, insert, soft delete,
 * undelete, and permanently delete weather location entities.
 */
@Dao
interface HorizonDatabaseDao {

    /**
     * Retrieves a list of weather location entities that are not marked as deleted.
     *
     * @return A [Flow] emitting a list of active (non-deleted) [SavedWeatherLocationEntity] entries.
     */
    @Query("SELECT * FROM savedweatherlocations WHERE isDeleted == 0")
    fun getAllWeatherEntitiesMarkedAsNotDeleted(): Flow<List<SavedWeatherLocationEntity>>

    /**
     * Retrieves a list of all weather location entities, including those marked as deleted.
     *
     * @return A [Flow] emitting a list of all [SavedWeatherLocationEntity] entries.
     */
    @Query("SELECT * FROM savedweatherlocations")
    fun getAllWeatherEntitiesIrrespectiveOfDeletedStatus(): Flow<List<SavedWeatherLocationEntity>>

    /**
     * Inserts or updates a weather location entity in the database.
     * If the entity already exists, it will be updated.
     *
     * @param weatherLocationEntity The entity to be inserted or updated.
     */
    @Upsert
    suspend fun addSavedWeatherEntity(weatherLocationEntity: SavedWeatherLocationEntity)

    /**
     * Marks a specific weather location entity as deleted by setting its `isDeleted` flag to 1.
     *
     * @param nameOfWeatherLocationEntity The name of the location to mark as deleted.
     */
    @Query("UPDATE savedweatherlocations SET isDeleted = 1 WHERE nameOfLocation = :nameOfWeatherLocationEntity")
    suspend fun markWeatherEntityAsDeleted(nameOfWeatherLocationEntity: String)

    /**
     * Unmarks a specific weather location entity as deleted by setting its `isDeleted` flag to 0.
     *
     * @param nameOfWeatherLocationEntity The name of the location to unmark as deleted.
     */
    @Query("UPDATE savedweatherlocations SET isDeleted = 0 WHERE nameOfLocation = :nameOfWeatherLocationEntity")
    suspend fun markWeatherEntityAsUnDeleted(nameOfWeatherLocationEntity: String)

    /**
     * Permanently deletes all weather location entries that are marked as deleted.
     */
    @Query("DELETE FROM savedweatherlocations WHERE isDeleted = 1")
    suspend fun deleteAllItemsMarkedAsDeleted()

    /**
     * Deletes a specific weather location entity from the database.
     *
     * @param weatherLocationEntity The entity to be permanently deleted.
     */
    @Delete
    suspend fun deleteSavedWeatherEntity(weatherLocationEntity: SavedWeatherLocationEntity)
}
