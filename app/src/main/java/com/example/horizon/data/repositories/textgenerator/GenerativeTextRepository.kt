package com.example.horizon.data.repositories.textgenerator

import com.example.horizon.domain.models.weather.CurrentWeatherDetails

/**
 * Repository interface for generating descriptive or informative text based on input parameters,
 * such as current weather conditions.
 */
interface GenerativeTextRepository {

    /**
     * Generates a descriptive text based on the given [weatherDetails].
     *
     * For a specific set of [CurrentWeatherDetails], the generated text is deterministic—meaning
     * if the same values are passed again, the same text will be returned. This can help
     * with caching or comparison purposes.
     *
     * @param weatherDetails The current weather data used to generate the text.
     * @return A [Result] containing the generated text on success, or an error if the operation fails.
     */
    suspend fun generateTextForWeatherDetails(weatherDetails: CurrentWeatherDetails): Result<String>
}
