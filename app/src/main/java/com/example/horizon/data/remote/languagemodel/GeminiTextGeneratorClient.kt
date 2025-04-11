package com.example.horizon.data.remote.languagemodel

import com.example.horizon.BuildConfig
import com.example.horizon.data.remote.languagemodel.models.GeneratedTextResponse
import com.example.horizon.data.remote.languagemodel.models.MessageDTO
import com.example.horizon.data.remote.languagemodel.models.TextGenerationPromptBody
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CancellationException
import okhttp3.internal.EMPTY_RESPONSE
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

/**
 * A concrete implementation of [TextGeneratorClient] that uses Google's Gemini model
 * to generate text responses based on a sequence of user/system messages.
 *
 * This client converts structured prompt messages into a plain text prompt and sends
 * it to the Gemini API, returning a formatted [GeneratedTextResponse].
 */
class GeminiTextGeneratorClient @Inject constructor() : TextGeneratorClient {

    // Instance of the Gemini generative model initialized with model name and API key
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = BuildConfig.GOOGLE_GEMINI_API_KEY
    )

    /**
     * Generates a response from the Gemini model based on the given conversation prompt.
     *
     * @param textGenerationPostBody The structured prompt body containing conversation history and model config.
     * @return A successful [Response] containing [GeneratedTextResponse] if the generation succeeds,
     *         or an error response if an exception occurs.
     */
    override suspend fun getModelResponseForConversations(
        textGenerationPostBody: TextGenerationPromptBody
    ): Response<GeneratedTextResponse> {
        return try {
            // Concatenates all prompt message contents into a single string to feed to Gemini
            val prompt = textGenerationPostBody.messages.fold("") { acc, messageDTO ->
                acc + " ${messageDTO.content}"
            }

            val defaultErrorMessage = "Sorry, I'm having trouble responding to you. Please try again."

            // Generate content from the Gemini model using the constructed prompt
            val generatedResponse = GeneratedTextResponse.GeneratedResponse(
                message = MessageDTO(
                    role = "", // Role isn't used in the response here
                    content = generativeModel.generateContent(prompt).text ?: defaultErrorMessage
                )
            )

            // Wrap the generated result with metadata into a full response object
            val currentTimeInSeconds = (System.currentTimeMillis() / 1000).toInt()
            val generatedTextResponse = GeneratedTextResponse(
                id = UUID.randomUUID().toString(),
                created = currentTimeInSeconds,
                generatedResponses = listOf(generatedResponse)
            )

            // Return a successful Retrofit response
            Response.success(generatedTextResponse)

        } catch (exception: Exception) {
            println(exception)
            // If coroutine was cancelled, propagate the exception
            if (exception is CancellationException) throw exception
            // Return a 400 error response in case of failure
            Response.error(400, EMPTY_RESPONSE)
        }
    }
}
