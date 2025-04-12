package com.project.horizon.data.repositories.weather

import com.project.horizon.data.getBodyOrThrowException
import com.project.horizon.data.local.weather.HorizonDatabaseDao
import com.project.horizon.data.local.weather.SavedWeatherLocationEntity
import com.project.horizon.data.remote.weather.WeatherClient
import com.project.horizon.domain.models.location.SavedLocation
import com.project.horizon.domain.models.location.toSavedLocation
import com.project.horizon.domain.models.weather.BriefWeatherDetails
import com.project.horizon.domain.models.weather.CurrentWeatherDetails
import com.project.horizon.domain.models.weather.HourlyForecast
import com.project.horizon.domain.models.weather.PrecipitationProbability
import com.project.horizon.domain.models.weather.SingleWeatherDetail
import com.project.horizon.domain.models.weather.toCurrentWeatherDetails
import com.project.horizon.domain.models.weather.toHourlyForecasts
import com.project.horizon.domain.models.weather.toPrecipitationProbabilities
import com.project.horizon.domain.models.weather.toSavedWeatherLocationEntity
import com.project.horizon.domain.models.weather.toSingleWeatherDetailList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * A concrete implementation of [WeatherRepository] responsible for handling
 * weather-related operations including fetching weather from remote APIs
 * and managing saved weather locations in the local database.
 */
class HorizonWeatherRepository @Inject constructor(
    private val weatherClient: WeatherClient,
    private val horizonDatabaseDao: HorizonDatabaseDao
) : WeatherRepository {

    /**
     * Fetches current weather details for a given location by querying the remote weather API.
     *
     * @param nameOfLocation The user-defined name for the location.
     * @param latitude Latitude of the location.
     * @param longitude Longitude of the location.
     * @return A [Result] containing [CurrentWeatherDetails] or an error.
     */
    override suspend fun fetchWeatherForLocation(
        nameOfLocation: String,
        latitude: String,
        longitude: String
    ): Result<CurrentWeatherDetails> = try {
        val response = weatherClient.getWeatherForCoordinates(
            latitude = latitude,
            longitude = longitude
        )
        Result.success(response.getBodyOrThrowException().toCurrentWeatherDetails(nameOfLocation))
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }

    /**
     * Returns a [Flow] stream of all saved locations that are not marked as deleted.
     */
    override fun getSavedLocationsListStream(): Flow<List<SavedLocation>> =
        horizonDatabaseDao.getAllWeatherEntitiesMarkedAsNotDeleted()
            .map { list -> list.map { it.toSavedLocation() } }

    /**
     * Saves a weather location in the local database.
     *
     * @param nameOfLocation User-defined label for the location.
     * @param latitude Latitude of the location.
     * @param longitude Longitude of the location.
     */
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

    /**
     * Soft deletes a location from the saved list by marking it as deleted in the local database.
     */
    override suspend fun deleteWeatherLocationFromSavedItems(briefWeatherLocation: BriefWeatherDetails) {
        val savedLocationEntity = briefWeatherLocation.toSavedWeatherLocationEntity()
        horizonDatabaseDao.markWeatherEntityAsDeleted(savedLocationEntity.nameOfLocation)
    }

    /**
     * Permanently deletes a saved weather location from the local database.
     */
    override suspend fun permanentlyDeleteWeatherLocationFromSavedItems(briefWeatherLocation: BriefWeatherDetails) {
        briefWeatherLocation.toSavedWeatherLocationEntity().run {
            horizonDatabaseDao.deleteSavedWeatherEntity(this)
        }
    }

    /**
     * Fetches hourly precipitation probabilities from the weather API for a given date range.
     *
     * @param latitude Latitude of the location.
     * @param longitude Longitude of the location.
     * @param dateRange Date range for which to fetch data.
     * @return A [Result] containing a list of [PrecipitationProbability] or an error.
     */
    override suspend fun fetchHourlyPrecipitationProbabilities(
        latitude: String,
        longitude: String,
        dateRange: ClosedRange<LocalDate>
    ): Result<List<PrecipitationProbability>> = try {
        val probabilities = weatherClient.getHourlyForecast(
            latitude = latitude,
            longitude = longitude,
            startDate = dateRange.start,
            endDate = dateRange.endInclusive
        ).getBodyOrThrowException().toPrecipitationProbabilities()
        Result.success(probabilities)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }

    /**
     * Fetches hourly forecasts for a given location and date range.
     */
    override suspend fun fetchHourlyForecasts(
        latitude: String,
        longitude: String,
        dateRange: ClosedRange<LocalDate>
    ): Result<List<HourlyForecast>> = try {
        val forecasts = weatherClient.getHourlyForecast(
            latitude = latitude,
            longitude = longitude,
            startDate = dateRange.start,
            endDate = dateRange.endInclusive
        ).getBodyOrThrowException().toHourlyForecasts()
        Result.success(forecasts)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }

    /**
     * Fetches additional weather info such as UV index, wind speed, etc. for the current day.
     */
    override suspend fun fetchAdditionalWeatherInfoItemsListForCurrentDay(
        latitude: String,
        longitude: String,
    ): Result<List<SingleWeatherDetail>> = try {
        val details = weatherClient.getAdditionalDailyForecastVariables(
            latitude = latitude,
            longitude = longitude,
            startDate = LocalDate.now(),
            endDate = LocalDate.now()
        ).getBodyOrThrowException().toSingleWeatherDetailList()
        Result.success(details)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }

    /**
     * Restores a previously deleted weather location by marking it as not deleted in the database.
     */
    override suspend fun tryRestoringDeletedWeatherLocation(nameOfLocation: String) {
        horizonDatabaseDao.markWeatherEntityAsUnDeleted(nameOfLocation)
    }
}
