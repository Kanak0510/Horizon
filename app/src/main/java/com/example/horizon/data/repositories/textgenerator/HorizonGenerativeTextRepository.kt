package com.example.horizon.data.repositories.textgenerator

import com.example.horizon.data.getBodyOrThrowException
import com.example.horizon.data.local.textgeneration.GeneratedTextCacheDatabaseDao
import com.example.horizon.data.local.textgeneration.GeneratedTextForLocationEntity
import com.example.horizon.data.remote.languagemodel.TextGeneratorClient
import com.example.horizon.data.remote.languagemodel.models.MessageDTO
import com.example.horizon.data.remote.languagemodel.models.TextGenerationPromptBody
import com.example.horizon.di.GeminiClient
import com.example.horizon.domain.models.weather.CurrentWeatherDetails
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/**
 * Concrete implementation of [GenerativeTextRepository] that utilizes a language model API
 * and local cache to generate and store descriptive weather texts.
 *
 * This class first checks a local database for a cached result matching the given
 * [CurrentWeatherDetails]. If a match is found, it returns the cached response.
 * Otherwise, it sends a prompt to the language model and stores the generated result
 * for future use.
 */
class HorizonGenerativeTextRepository @Inject constructor(
    @GeminiClient private val textGeneratorClient: TextGeneratorClient,
    private val generatedTextCacheDatabaseDao: GeneratedTextCacheDatabaseDao
) : GenerativeTextRepository {

    /**
     * Generates a whimsical, short description of the weather based on [weatherDetails].
     *
     * - First, attempts to find a cached result in the local Room database using
     *   the location name, temperature, and weather condition as keys.
     * - If no cache is found, sends a prompt to the text generation model (e.g., GPT-4o),
     *   receives a generated response, caches it locally, and returns it.
     * - Uses a combination of system and user prompts to guide the model's output.
     *
     * @param weatherDetails Current weather data used for generating text.
     * @return A [Result] containing the generated description or an error if the operation fails.
     */
    override suspend fun generateTextForWeatherDetails(weatherDetails: CurrentWeatherDetails): Result<String> {
        // Check for existing cached text
        val generatedTextEntity = generatedTextCacheDatabaseDao.getSavedGeneratedTextForDetails(
            nameOfLocation = weatherDetails.nameOfLocation,
            temperature = weatherDetails.temperatureRoundedToInt,
            conciseWeatherDescription = weatherDetails.weatherCondition
        )
        if (generatedTextEntity != null) return Result.success(generatedTextEntity.generatedDescription)

        // Construct system and user prompts
        val systemPrompt = """
            You are a weather reporter. Generate a very short, but whimsical description of the weather,
            based on the given information.
        """.trimIndent()
        val userPrompt = """
            location = ${weatherDetails.nameOfLocation};
            currentTemperature = ${weatherDetails.temperatureRoundedToInt};
            weatherCondition = ${weatherDetails.weatherCondition};
            isNight = ${weatherDetails.isDay != 1}
        """.trimIndent()

        val promptMessages = listOf(
            MessageDTO(role = MessageDTO.Roles.SYSTEM, content = systemPrompt),
            MessageDTO(role = MessageDTO.Roles.USER, content = userPrompt)
        )

        val textGenerationPrompt = TextGenerationPromptBody(
            messages = promptMessages,
            model = "gpt-4o"
        )

        // Make request to language model and handle response
        return try {
            val generatedTextResponse = textGeneratorClient.getModelResponseForConversations(
                textGenerationPostBody = textGenerationPrompt
            ).getBodyOrThrowException()
                .generatedResponses
                .first().message.content

            // Cache the response
            val generatedTextForLocationEntity = GeneratedTextForLocationEntity(
                nameOfLocation = weatherDetails.nameOfLocation,
                temperature = weatherDetails.temperatureRoundedToInt,
                conciseWeatherDescription = weatherDetails.weatherCondition,
                generatedDescription = generatedTextResponse
            )
            generatedTextCacheDatabaseDao.addGeneratedTextForLocation(generatedTextForLocationEntity)

            Result.success(generatedTextResponse)
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }
}
