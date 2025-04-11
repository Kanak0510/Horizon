package com.example.horizon.domain.models.weather

import com.example.horizon.data.remote.weather.models.HourlyWeatherInfoResponse
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Represents the precipitation probability forecast for a specific time and location.
 *
 * @property latitude The latitude of the location as a [String].
 * @property longitude The longitude of the location as a [String].
 * @property dateTime The date and time of the forecast as a [LocalDateTime].
 * @property probabilityPercentage The chance of precipitation, as a percentage.
 */
data class PrecipitationProbability(
    val latitude: String,
    val longitude: String,
    val dateTime: LocalDateTime,
    val probabilityPercentage: Int
)

/**
 * Maps an [HourlyWeatherInfoResponse] from the data layer to a list of [PrecipitationProbability] domain models.
 *
 * @return A list of [PrecipitationProbability] items representing the precipitation forecast.
 */
fun HourlyWeatherInfoResponse.toPrecipitationProbabilities(): List<PrecipitationProbability> {
    return hourlyForecast.timestamps.indices.map { i ->
        val epochSeconds = hourlyForecast.timestamps[i].toLong()
        val correspondingLocalDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(epochSeconds),
            ZoneId.systemDefault()
        )

        PrecipitationProbability(
            latitude = latitude,
            longitude = longitude,
            dateTime = correspondingLocalDateTime,
            probabilityPercentage = hourlyForecast.precipitationProbabilityPercentages[i]
        )
    }
}
