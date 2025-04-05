package com.example.horizon.data.repositories.textgenerator

import com.example.horizon.data.getBodyOrThrowException
import com.example.horizon.data.remote.languagemodel.TextGeneratorClient
import com.example.horizon.data.remote.languagemodel.models.MessageDTO
import com.example.horizon.data.remote.languagemodel.models.TextGenerationPromptBody
import com.example.horizon.domain.models.CurrentWeatherDetails
import kotlinx.coroutines.CancellationException
import javax.inject.Inject


class HorizonGenerativeTextRepository @Inject constructor(
    private val textGeneratorClient: TextGeneratorClient
) : GenerativeTextRepository {

    private val cache = mutableMapOf<CacheKey, String>()

    override suspend fun generateTextForWeatherDetails(weatherDetails: CurrentWeatherDetails): Result<String> {
        val cacheKey = getCacheKey(weatherDetails)
        if (cacheKey in cache.keys) return Result.success(cache.getValue(cacheKey))
        // Prompts
        val systemPrompt = """
            You are a weather reporter. Generate a very short, but whimsical description of the weather,
            based on the given information.
        """.trimIndent()
        val userPrompt = """
            location = ${weatherDetails.nameOfLocation};
            currentTemperature = ${weatherDetails.temperatureRoundedToInt};
            weatherCondition = ${weatherDetails.weatherCondition};
        """.trimIndent()
        // Prompt Messages
        val promptMessages = listOf(
            MessageDTO(role = "system", content = systemPrompt),
            MessageDTO(role = "user", content = userPrompt)
        )
        val textGenerationPrompt = TextGenerationPromptBody(
            messages = promptMessages,
            model = "gpt-4o"
        )
        // Request to generate text based on prompt body
        return try {
            val generatedTextResponse = textGeneratorClient.getModelResponseForConversations(
                textGenerationPostBody = textGenerationPrompt
            ).getBodyOrThrowException()
                .generatedResponses
                .first().message
                .content.also { cache[cacheKey] = it }
            Result.success(generatedTextResponse)
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }

    private fun getCacheKey(weatherDetails: CurrentWeatherDetails): CacheKey {
        val keyValue =
            "${weatherDetails.nameOfLocation};${weatherDetails.temperatureRoundedToInt};${weatherDetails.weatherCondition}"
        return CacheKey(value = keyValue)
    }

    @JvmInline
    private value class CacheKey(val value: String)
}