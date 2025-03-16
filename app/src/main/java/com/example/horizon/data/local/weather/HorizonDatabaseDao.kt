package com.example.horizon.data.local.weather

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HorizonDatabaseDao {

    @Query("Select * from SavedWeatherLocations")
    fun getAllSavedWeatherEntities(): Flow<List<SavedWeatherLocationEntity>>

    @Upsert
    suspend fun addSavedWeatherEntity(weatherLocationEntity: SavedWeatherLocationEntity)
}