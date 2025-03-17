package com.example.horizon.data.local.weather

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class HorizonDatabaseTest {
    private lateinit var database: HorizonDatabase
    private lateinit var dao: HorizonDatabaseDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            HorizonDatabase::class.java
        ).build()
        dao = database.getDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addSavedWeatherEntityTest_ValidEntity_isSuccessfullySaved() = runTest {
        val weatherLocationEntity = SavedWeatherLocationEntity(
            id = "1",
            nameOfLocation = "New York",
            latitude = "40.7128",
            longitude = "74.0060"
        )
        dao.addSavedWeatherEntity(weatherLocationEntity)
        with(dao.getAllSavedWeatherEntities().first()) {
            assert(size == 1)
            assert(first() == weatherLocationEntity)
        }
    }

    @Test
    fun deleteSavedWeatherEntityTest_ValidExistingEntity_isSuccessfullyDeleted() = runTest {
        val weatherLocationEntity = SavedWeatherLocationEntity(
            id = "1",
            nameOfLocation = "Seattle",
            latitude = "47.6062",
            longitude = "-122.3321"
        )
        with(dao){
            // Add item to database
            addSavedWeatherEntity(weatherLocationEntity)
            // Item must be inserted
            assert(getAllSavedWeatherEntities().first().contains(weatherLocationEntity))
            // Delete item from database
            deleteSavedWeatherEntity(weatherLocationEntity)
            // Item must not exist in the database
            assert(!getAllSavedWeatherEntities().first().contains(weatherLocationEntity))
        }
    }

}