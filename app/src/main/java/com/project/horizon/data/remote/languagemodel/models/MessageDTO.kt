package com.project.horizon.data.remote.languagemodel.models

import kotlinx.serialization.Serializable

/**
 * Represents a single message in a conversation between the client and a large language model (LLM) API.
 * Each message contains a role (e.g., user or system) and the corresponding content.
 *
 * This class is used for both sending requests to and receiving responses from the LLM API.
 *
 * @property role The role of the message sender, such as "user" or "system".
 * @property content The actual text content of the message.
 */
@Serializable
data class MessageDTO(
    val role: String,   // Sender role: either "system" or "user"
    val content: String // The message content
) {
    /**
     * Common role constants used in communication with the language model.
     * Helps avoid hardcoding string literals throughout the codebase.
     */
    object Roles {
        const val SYSTEM = "system" // Represents the system (or assistant) role
        const val USER = "user"     // Represents the user role
    }
}
