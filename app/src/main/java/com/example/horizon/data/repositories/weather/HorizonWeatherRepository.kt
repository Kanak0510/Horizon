package com.example.horizon.data.repositories.weather

import com.example.horizon.data.getBodyOrThrowException
import com.example.horizon.data.local.weather.HorizonDatabaseDao
import com.example.horizon.data.local.weather.SavedWeatherLocationEntity
import com.example.horizon.data.remote.weather.WeatherClient
import com.example.horizon.data.remote.weather.WeatherClientConstants
import com.example.horizon.data.remote.weather.models.toWeatherDetails
import com.example.horizon.domain.models.BriefWeatherDetails
import com.example.horizon.domain.models.WeatherDetails
import com.example.horizon.domain.models.toBriefWeatherDetails
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * The default concrete implementation of [WeatherRepository].
 */
class HorizonWeatherRepository @Inject constructor(
    private val weatherClient: WeatherClient,
    private val horizonDatabaseDao: HorizonDatabaseDao
) : WeatherRepository {

    override suspend fun fetchWeatherForLocation(
        latitude: String,
        longitude: String
    ): Result<WeatherDetails> = try {
        val response = weatherClient.getWeatherForCoordinates(
            latitude = latitude,
            longitude = longitude,
            units = WeatherClientConstants.Units.CELSIUS // todo
        )
        Result.success(response.getBodyOrThrowException().toWeatherDetails())
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }

    override fun getWeatherStreamForPreviouslySavedLocations(): Flow<List<BriefWeatherDetails>> {
        return horizonDatabaseDao.getAllSavedWeatherEntities()
            .map { savedWeatherLocationEntities ->
                savedWeatherLocationEntities.map {
                    fetchWeatherForLocation(latitude = it.latitude, longitude = it.longitude)
                }
            }.map { weatherDetailResults ->
                weatherDetailResults.mapNotNull { weatherDetailsResult ->
                    weatherDetailsResult.getOrNull()?.toBriefWeatherDetails()
                }
            }
    }

    override suspend fun saveWeatherLocation(
        nameOfLocation: String,
        latitude: String,
        longitude: String
    ) {
        val savedWeatherEntity = SavedWeatherLocationEntity(
            nameOfLocation = nameOfLocation,
            latitude = latitude,
            longitude = longitude
        )
        horizonDatabaseDao.addSavedWeatherEntity(savedWeatherEntity)
    }
}