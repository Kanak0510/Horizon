package com.project.horizon.data.remote.location.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a response from a location suggestion API, typically used for autocomplete or search.
 *
 * @property suggestions A list of [Suggestion] objects that match the search query.
 */
@Serializable
data class SuggestionsResponse(
    @SerialName("results") val suggestions: List<Suggestion> = emptyList()
) {

    /**
     * Represents a single suggestion result returned by the location suggestion API.
     *
     * @property idOfPlace The unique identifier of the suggested place.
     * @property nameOfPlace The name of the place (e.g., city or town).
     * @property country The country where the place is located.
     * @property state The administrative division or state (nullable).
     * @property countryCode The ISO 3166-1 alpha-2 country code (nullable).
     * @property latitude The latitude coordinate of the place.
     * @property longitude The longitude coordinate of the place.
     */
    @Serializable
    data class Suggestion(
        @SerialName("id") val idOfPlace: String,
        @SerialName("name") val nameOfPlace: String,
        @SerialName("country") val country: String?,
        @SerialName("admin1") val state: String?,
        @SerialName("country_code") val countryCode: String?,
        val latitude: String,
        val longitude: String
    )
}
