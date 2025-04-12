package com.project.horizon.data.local.textgeneration

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room Database class for storing generated text descriptions based on weather conditions.
 *
 * This database holds one table: [GeneratedTextForLocationEntity], which stores cached
 * generated text results for specific locations and weather conditions.
 *
 * @see GeneratedTextForLocationEntity
 * @see GeneratedTextCacheDatabaseDao
 */
@Database(entities = [GeneratedTextForLocationEntity::class], version = 1)
abstract class HorizonGeneratedTextCacheDatabase : RoomDatabase() {

    /**
     * Abstract method to get the DAO (Data Access Object) for performing
     * operations on the generated text cache table.
     *
     * @return An implementation of [GeneratedTextCacheDatabaseDao]
     */
    abstract fun getDao(): GeneratedTextCacheDatabaseDao

    companion object {
        /**
         * Name of the Room database file used to store the generated text cache.
         */
        const val DATABASE_NAME = "Generated_Text_Cache_Database"
    }
}
