package com.example.horizon.data.local.weather

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SavedWeatherLocationEntity::class], version = 1)
abstract class HorizonDatabase : RoomDatabase() {

    abstract fun getDao(): HorizonDatabaseDao

    companion object {
        const val DATABASE_NAME = "Weather_Database"
    }
}