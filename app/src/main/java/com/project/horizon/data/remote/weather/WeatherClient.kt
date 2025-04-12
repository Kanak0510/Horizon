package com.project.horizon.data.remote.weather

import com.project.horizon.data.remote.weather.models.AdditionalDailyForecastVariablesResponse
import com.project.horizon.data.remote.weather.models.CurrentWeatherResponse
import com.project.horizon.data.remote.weather.models.HourlyWeatherInfoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate

/**
 * An interface representing a remote weather data provider.
 * Defines methods for fetching current weather, hourly forecast, and daily forecast variables.
 */
interface WeatherClient {

    /**
     * Fetches the current weather for a given geographic location.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param temperatureUnit Unit for representing temperature values.
     * @param windSpeedUnit Unit for representing wind speed values.
     * @param precipitationUnit Unit for representing precipitation values.
     * @param shouldIncludeCurrentWeatherInformation Must be set to `true` to include current weather data.
     * @return A [Response] containing the [CurrentWeatherResponse].
     */
    @GET(WeatherClientConstants.EndPoints.FORECAST)
    suspend fun getWeatherForCoordinates(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("temperature_unit") temperatureUnit: WeatherClientConstants.TemperatureUnits = WeatherClientConstants.TemperatureUnits.CELSIUS,
        @Query("windspeed_unit") windSpeedUnit: WeatherClientConstants.WindSpeedUnit = WeatherClientConstants.WindSpeedUnit.KILOMETERS_PER_HOUR,
        @Query("precipitation_unit") precipitationUnit: WeatherClientConstants.PrecipitationUnit = WeatherClientConstants.PrecipitationUnit.INCHES,
        @Query("current_weather") shouldIncludeCurrentWeatherInformation: Boolean = true
    ): Response<CurrentWeatherResponse>

    /**
     * Retrieves hourly weather forecast data for the specified location and time range.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param startDate The start date of the forecast range (format: YYYY-MM-DD).
     * @param endDate The end date of the forecast range (format: YYYY-MM-DD).
     * @param timezoneConfiguration The timezone to apply to the forecast timestamps.
     * @param precipitationUnit Unit for representing precipitation values.
     * @param timeFormat The format used for the time values in the response.
     * @param hourlyForecastsToReturn Specifies which hourly forecast fields to include.
     * @return A [Response] containing the [HourlyWeatherInfoResponse].
     */
    @GET(WeatherClientConstants.EndPoints.FORECAST)
    suspend fun getHourlyForecast(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("start_date") startDate: LocalDate,
        @Query("end_date") endDate: LocalDate,
        @Query("timezone") timezoneConfiguration: WeatherClientConstants.TimeZoneConfiguration = WeatherClientConstants.TimeZoneConfiguration.LOCAL_DEVICE_TIMEZONE,
        @Query("precipitation_unit") precipitationUnit: WeatherClientConstants.PrecipitationUnit = WeatherClientConstants.PrecipitationUnit.INCHES,
        @Query("timeformat") timeFormat: WeatherClientConstants.TimeFormats = WeatherClientConstants.TimeFormats.UNIX_EPOCH_TIME_IN_SECONDS,
        @Query("hourly") hourlyForecastsToReturn: WeatherClientConstants.HourlyForecastItems = WeatherClientConstants.HourlyForecastItems.ALL
    ): Response<HourlyWeatherInfoResponse>

    /**
     * Fetches additional daily weather forecast variables for a given location and date range.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param startDate Start date of the forecast range (format: YYYY-MM-DD).
     * @param endDate End date of the forecast range (format: YYYY-MM-DD).
     * @param timezoneConfiguration Timezone for aligning forecast timestamps.
     * @param timeFormat The format of time values in the response.
     * @param dailyForecastsToReturn Specifies which daily forecast fields to include.
     * @return A [Response] containing the [AdditionalDailyForecastVariablesResponse].
     */
    @GET(WeatherClientConstants.EndPoints.FORECAST)
    suspend fun getAdditionalDailyForecastVariables(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("start_date") startDate: LocalDate,
        @Query("end_date") endDate: LocalDate,
        @Query("timezone") timezoneConfiguration: WeatherClientConstants.TimeZoneConfiguration = WeatherClientConstants.TimeZoneConfiguration.DEFAULT_FOR_GIVEN_COORDINATES,
        @Query("timeformat") timeFormat: WeatherClientConstants.TimeFormats = WeatherClientConstants.TimeFormats.UNIX_EPOCH_TIME_IN_SECONDS,
        @Query("daily") dailyForecastsToReturn: WeatherClientConstants.DailyForecastItems = WeatherClientConstants.DailyForecastItems.ALL
    ): Response<AdditionalDailyForecastVariablesResponse>
}
