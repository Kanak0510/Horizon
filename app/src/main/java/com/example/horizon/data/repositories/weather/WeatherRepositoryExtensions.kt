package com.example.horizon.data.repositories.weather

import com.example.horizon.domain.models.weather.HourlyForecast
import com.example.horizon.domain.models.weather.PrecipitationProbability
import kotlinx.coroutines.CancellationException
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Fetches a list of [PrecipitationProbability] values for the next 24 hours
 * starting from the current time, for the specified [latitude] and [longitude].
 *
 * @param latitude The latitude of the location.
 * @param longitude The longitude of the location.
 * @return A [Result] containing a list of up to 24 [PrecipitationProbability] entries, or an error.
 */
suspend fun WeatherRepository.fetchPrecipitationProbabilitiesForNext24hours(
    latitude: String,
    longitude: String,
): Result<List<PrecipitationProbability>> {
    return try {
        val now = LocalDateTime.now()
        val filtered = this.fetchHourlyPrecipitationProbabilities(
            latitude = latitude,
            longitude = longitude,
            dateRange = LocalDate.now()..LocalDate.now().plusDays(1)
        ).getOrThrow().filter { it.dateTime.isAfter(now) || it.dateTime.toLocalTime() == now.toLocalTime() }
            .take(24)

        Result.success(filtered)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }
}

/**
 * Fetches a list of [HourlyForecast] values for the next 24 hours
 * starting from the current time, for the specified [latitude] and [longitude].
 *
 * @param latitude The latitude of the location.
 * @param longitude The longitude of the location.
 * @return A [Result] containing a list of up to 24 [HourlyForecast] entries, or an error.
 */
suspend fun WeatherRepository.fetchHourlyForecastsForNext24Hours(
    latitude: String,
    longitude: String,
): Result<List<HourlyForecast>> {
    return try {
        val now = LocalDateTime.now()
        val filtered = this.fetchHourlyForecasts(
            latitude = latitude,
            longitude = longitude,
            dateRange = LocalDate.now()..LocalDate.now().plusDays(1)
        ).getOrThrow().filter { it.dateTime.isAfter(now) || it.dateTime.toLocalTime() == now.toLocalTime() }
            .take(24)

        Result.success(filtered)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }
}
