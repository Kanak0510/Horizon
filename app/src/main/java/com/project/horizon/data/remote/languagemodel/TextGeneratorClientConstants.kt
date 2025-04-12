package com.project.horizon.data.remote.languagemodel

/**
 * Contains constants related to the configuration and endpoint paths
 * used by the [TextGeneratorClient] to communicate with the LLM API.
 */
object TextGeneratorClientConstants {

    /**
     * The base URL for the text generation API.
     * This is typically used when initializing Retrofit.
     */
    const val BASE_URL = "https://api.openai.com/v1/chat/"

    /**
     * Holds endpoint paths for various LLM API operations.
     */
    object Endpoints {
        /**
         * The specific endpoint path used for generating text completions.
         * Full URL: BASE_URL + CHAT_COMPLETION_END_POINT
         */
        const val CHAT_COMPLETION_END_POINT = "completions"
    }
}
