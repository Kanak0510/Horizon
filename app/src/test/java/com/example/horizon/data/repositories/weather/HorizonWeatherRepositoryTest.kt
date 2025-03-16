package com.example.horizon.data.repositories.weather

import com.example.horizon.data.local.weather.HorizonDatabaseDao
import com.example.horizon.data.local.weather.SavedWeatherLocationEntity
import com.example.horizon.di.NetworkModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class HorizonWeatherRepositoryTest {

    private lateinit var weatherRepository: HorizonWeatherRepository
    private val savedLocations = listOf(
        SavedWeatherLocationEntity(
            id = "1",
            nameOfLocation = "Seattle",
            latitude = "47.6062",
            longitude = "-122.3321"
        ),
        SavedWeatherLocationEntity(
            id = "2",
            nameOfLocation = "New York",
            latitude = "40.7128",
            longitude = "-74.0060"
        ),
        SavedWeatherLocationEntity(
            id = "3",
            nameOfLocation = "London",
            latitude = "51.5074",
            longitude = "-0.1278"
        )
    )

    @Before
    fun setup() {
        val daoMock = mock<HorizonDatabaseDao> {
            onBlocking { getAllSavedWeatherEntities() } doAnswer {
                flowOf(savedLocations)
            }
        }
        weatherRepository = HorizonWeatherRepository(
            weatherClient = NetworkModule.provideWeatherClient(),
            horizonDatabaseDao = daoMock
        )
    }

    @Test
    fun `getWeatherForLocation should successfully fetch weather details for a given valid coordinate`() =
        runTest {
            val latitude = "37.422131"
            val longitude = "-122.084801"
            val result = weatherRepository.fetchWeatherForLocation(latitude, longitude)
            assert(result.isSuccess)
            assert(result.getOrNull() != null)
        }

    @Test
    fun `getWeatherForLocation should return an exception for an invalid coordinate`() = runTest {
        /**
         * This is an invalid coordinate because the latitude and longitude values are outside the
         * valid range of -90 to 90 and -180 to 180 degrees, respectively.
         */
        val latitude = "1000.0"
        val longitude = "-2000.0"
        val result = weatherRepository.fetchWeatherForLocation(latitude, longitude)
        assert(result.isFailure)
        assert(result.exceptionOrNull() != null)
    }
}