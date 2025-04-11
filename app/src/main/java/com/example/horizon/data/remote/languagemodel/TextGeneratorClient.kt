package com.example.horizon.data.remote.languagemodel

import com.example.horizon.data.remote.languagemodel.models.GeneratedTextResponse
import com.example.horizon.data.remote.languagemodel.models.TextGenerationPromptBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Defines an abstraction for a text generation API client that communicates
 * with a large language model (LLM) to generate responses based on provided prompts.
 *
 * This interface allows flexibility to plug in different LLM providers (e.g., OpenAI, Gemini)
 * by implementing the contract defined here.
 */
interface TextGeneratorClient {

    /**
     * Sends a request to the LLM with a structured prompt and receives a generated response.
     *
     * @param textGenerationPostBody The prompt body, including messages and model config.
     * @return A [Response] containing a [GeneratedTextResponse] object, or an error if generation fails.
     */
    @POST(TextGeneratorClientConstants.Endpoints.CHAT_COMPLETION_END_POINT)
    suspend fun getModelResponseForConversations(
        @Body textGenerationPostBody: TextGenerationPromptBody
    ): Response<GeneratedTextResponse>
}
