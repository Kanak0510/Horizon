package com.project.horizon.data.repositories.weather

import com.project.horizon.domain.models.location.SavedLocation
import com.project.horizon.domain.models.weather.BriefWeatherDetails
import com.project.horizon.domain.models.weather.CurrentWeatherDetails
import com.project.horizon.domain.models.weather.HourlyForecast
import com.project.horizon.domain.models.weather.PrecipitationProbability
import com.project.horizon.domain.models.weather.SingleWeatherDetail
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * A repository interface responsible for managing all weather-related operations including
 * data fetching, storage, and retrieval of saved locations.
 */
interface WeatherRepository {

    /**
     * Fetches current weather details for the specified location.
     *
     * @param nameOfLocation A user-defined label for the location.
     * @param latitude Latitude coordinate of the location.
     * @param longitude Longitude coordinate of the location.
     * @return A [Result] containing [CurrentWeatherDetails] on success, or an exception on failure.
     */
    suspend fun fetchWeatherForLocation(
        nameOfLocation: String,
        latitude: String,
        longitude: String
    ): Result<CurrentWeatherDetails>

    /**
     * Returns a reactive [Flow] emitting the list of [SavedLocation]s stored locally
     * that are not marked as deleted.
     */
    fun getSavedLocationsListStream(): Flow<List<SavedLocation>>

    /**
     * Saves a weather location with the given parameters to local storage.
     *
     * @param nameOfLocation A user-defined name for the location.
     * @param latitude Latitude of the location.
     * @param longitude Longitude of the location.
     */
    suspend fun saveWeatherLocation(nameOfLocation: String, latitude: String, longitude: String)

    /**
     * Soft-deletes a saved weather location, marking it as deleted without removing it
     * from the database. This allows for potential restoration.
     *
     * @param briefWeatherLocation The [BriefWeatherDetails] of the location to delete.
     */
    suspend fun deleteWeatherLocationFromSavedItems(briefWeatherLocation: BriefWeatherDetails)

    /**
     * Permanently deletes a saved weather location from the database.
     * Use [deleteWeatherLocationFromSavedItems] instead if you want the option to restore later.
     *
     * @param briefWeatherLocation The [BriefWeatherDetails] representing the location to delete.
     */
    suspend fun permanentlyDeleteWeatherLocationFromSavedItems(briefWeatherLocation: BriefWeatherDetails)

    /**
     * Attempts to restore a location that was previously soft-deleted.
     *
     * @param nameOfLocation The name of the location to restore.
     */
    suspend fun tryRestoringDeletedWeatherLocation(nameOfLocation: String)

    /**
     * Fetches hourly precipitation probabilities for the given coordinates and date range.
     *
     * @param latitude Latitude of the location.
     * @param longitude Longitude of the location.
     * @param dateRange The range of dates to retrieve data for. Defaults to today and tomorrow.
     * @return A [Result] containing a list of [PrecipitationProbability] or an error.
     */
    suspend fun fetchHourlyPrecipitationProbabilities(
        latitude: String,
        longitude: String,
        dateRange: ClosedRange<LocalDate> = LocalDate.now()..LocalDate.now().plusDays(1)
    ): Result<List<PrecipitationProbability>>

    /**
     * Retrieves hourly weather forecasts for the specified location and date range.
     *
     * @param latitude Latitude of the location.
     * @param longitude Longitude of the location.
     * @param dateRange Range of dates for which forecasts are required.
     * @return A [Result] containing a list of [HourlyForecast]s or an exception.
     */
    suspend fun fetchHourlyForecasts(
        latitude: String,
        longitude: String,
        dateRange: ClosedRange<LocalDate>
    ): Result<List<HourlyForecast>>

    /**
     * Fetches additional weather details such as UV index, dew point, and visibility for the current day.
     *
     * @param latitude Latitude of the location.
     * @param longitude Longitude of the location.
     * @return A [Result] containing a list of [SingleWeatherDetail] objects or an error.
     */
    suspend fun fetchAdditionalWeatherInfoItemsListForCurrentDay(
        latitude: String,
        longitude: String
    ): Result<List<SingleWeatherDetail>>
}
