package com.example.horizon.data.remote.languagemodel.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the request body sent to a large language model API for text generation.
 *
 * This class holds a list of prompt messages along with model configuration and metadata
 * that guides how the language model should generate a response.
 *
 * @property messages A list of messages (e.g., user and system messages) forming the prompt history.
 * @property model The identifier of the language model to be used (e.g., "gpt-3.5-turbo").
 * @property maxResponseTokens The maximum number of tokens the model is allowed to generate in response.
 *                             This is serialized as `max_tokens` to match the API format.
 *
 * @see MessageDTO
 * @see GeneratedTextResponse
 */
@Serializable
data class TextGenerationPromptBody(
    val messages: List<MessageDTO>,                // Conversation history used as prompt
    val model: String,                             // Name of the model to use
    @SerialName("max_tokens")
    val maxResponseTokens: Int = 150               // Max number of tokens allowed in the response
)
