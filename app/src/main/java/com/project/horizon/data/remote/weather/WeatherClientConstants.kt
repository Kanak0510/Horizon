package com.project.horizon.data.remote.weather

import com.project.horizon.data.remote.weather.WeatherClientConstants.TimeZoneConfiguration.DEFAULT_FOR_GIVEN_COORDINATES
import com.project.horizon.data.remote.weather.WeatherClientConstants.TimeZoneConfiguration.LOCAL_DEVICE_TIMEZONE
import java.time.ZoneId

/**
 * Contains constants and enums used by the [WeatherClient] to configure
 * API requests for fetching weather data.
 */
object WeatherClientConstants {

    /**
     * Base URL of the Open-Meteo API.
     */
    const val BASE_URL = "https://api.open-meteo.com/v1/"

    /**
     * API endpoints provided by the Open-Meteo service.
     */
    object EndPoints {
        const val FORECAST = "forecast"
    }

    /**
     * Supported temperature units for weather data.
     */
    enum class TemperatureUnits(private val valueToBeSentToTheApi: String) {
        CELSIUS("celsius"),
        FAHRENHEIT("fahrenheit");

        override fun toString(): String = valueToBeSentToTheApi
    }

    /**
     * Supported wind speed units for weather data.
     */
    enum class WindSpeedUnit(private val valueToBeSentToTheApi: String) {
        KILOMETERS_PER_HOUR("kmh"),
        MILES_PER_HOUR("mph");

        override fun toString(): String = valueToBeSentToTheApi
    }

    /**
     * Supported precipitation units for weather data.
     */
    enum class PrecipitationUnit(private val valueToBeSentToTheApi: String) {
        MILLIMETERS("mm"),
        INCHES("inch");

        override fun toString(): String = valueToBeSentToTheApi
    }

    /**
     * Available daily forecast data fields supported by the API.
     */
    enum class DailyForecastItems(private val valueToBeSentToTheApi: String) {
        MAX_TEMPERATURE("temperature_2m_max"),
        MIN_TEMPERATURE("temperature_2m_min"),
        MAX_APPARENT_TEMPERATURE("apparent_temperature_max"),
        MIN_APPARENT_TEMPERATURE("apparent_temperature_min"),
        SUNRISE("sunrise"),
        SUNSET("sunset"),
        UV_INDEX("uv_index_max"),
        WIND_SPEED("windspeed_10m_max"),
        WIND_DIRECTION("winddirection_10m_dominant"),

        /**
         * All available daily forecast data fields, combined as a single string.
         */
        ALL(
            "${MAX_TEMPERATURE},$MIN_TEMPERATURE," +
                    "$MAX_APPARENT_TEMPERATURE,$MIN_APPARENT_TEMPERATURE," +
                    "$SUNRISE,$SUNSET,$UV_INDEX,$WIND_SPEED,${WIND_DIRECTION}"
        );

        override fun toString(): String = valueToBeSentToTheApi
    }

    /**
     * Available hourly forecast data fields supported by the API.
     */
    enum class HourlyForecastItems(private val valueToBeSentToTheApi: String) {
        PRECIPITATION_PROBABILITIES("precipitation_probability"),
        WEATHER_CODE("weathercode"),
        TEMPERATURE("temperature_2m"),

        /**
         * All available hourly forecast data fields, combined as a single string.
         */
        ALL("$WEATHER_CODE,$PRECIPITATION_PROBABILITIES,$TEMPERATURE");

        override fun toString(): String = valueToBeSentToTheApi
    }

    /**
     * Time formats supported by the API for weather data timestamps.
     */
    enum class TimeFormats(private val valueToBeSentToTheApi: String) {
        /**
         * Unix epoch time format (seconds since 1970-01-01 UTC).
         */
        UNIX_EPOCH_TIME_IN_SECONDS("unixtime"),

        /**
         * ISO 8601 standard datetime format.
         */
        ISO_8601("iso8601");

        override fun toString(): String = valueToBeSentToTheApi
    }

    /**
     * Timezone configuration options for aligning forecast timestamps.
     *
     * **Note:** Prefer using [DEFAULT_FOR_GIVEN_COORDINATES] for weather-related data.
     * Avoid using [LOCAL_DEVICE_TIMEZONE] unless absolutely necessary, as it can lead to
     * incorrect interpretations of sunrise, sunset, and other time-dependent weather fields.
     */
    enum class TimeZoneConfiguration(private val valueToBeSentToTheApi: String) {
        /**
         * Automatically resolves to the timezone of the provided coordinates.
         */
        DEFAULT_FOR_GIVEN_COORDINATES("auto"),

        /**
         * Uses the device's current system timezone.
         * **Warning:** This may lead to inaccurate results for location-based weather data.
         */
        LOCAL_DEVICE_TIMEZONE(ZoneId.systemDefault().toString());

        override fun toString(): String = valueToBeSentToTheApi
    }
}
