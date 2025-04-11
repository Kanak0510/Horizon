package com.example.horizon.data.local.weather

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room Database class for storing weather-related data, specifically
 * saved weather locations.
 *
 * This database contains one table: [SavedWeatherLocationEntity], which
 * stores information about user-saved locations for weather tracking.
 *
 * @see SavedWeatherLocationEntity
 * @see HorizonDatabaseDao
 */
@Database(entities = [SavedWeatherLocationEntity::class], version = 1)
abstract class HorizonDatabase : RoomDatabase() {

    /**
     * Provides access to the DAO (Data Access Object) for performing
     * operations on the saved weather locations table.
     *
     * @return An implementation of [HorizonDatabaseDao]
     */
    abstract fun getDao(): HorizonDatabaseDao

    companion object {
        /**
         * Name of the Room database file used to store weather location data.
         */
        const val DATABASE_NAME = "Weather_Database"
    }
}
