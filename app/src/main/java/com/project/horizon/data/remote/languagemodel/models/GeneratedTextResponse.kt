package com.project.horizon.data.remote.languagemodel.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the response structure from a large language model API.
 * This response typically includes metadata such as an ID, creation timestamp,
 * and a list of generated response choices.
 *
 * @property id Unique identifier for the response.
 * @property created Timestamp indicating when the response was generated.
 * @property generatedResponses A list of generated outputs, typically one or more message options.
 */
@Serializable
data class GeneratedTextResponse(
    val id: String,                               // Unique ID for the response
    val created: Int,                             // Creation timestamp (usually Unix time)

    @SerialName("choices")
    val generatedResponses: List<GeneratedResponse> // List of generated response choices
) {
    /**
     * Represents a single generated response choice from the language model.
     *
     * @property message The content of the generated message, modeled by [MessageDTO].
     */
    @Serializable
    data class GeneratedResponse(
        val message: MessageDTO // The generated message object
    )
}
