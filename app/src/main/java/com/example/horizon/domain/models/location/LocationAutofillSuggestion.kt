package com.example.horizon.domain.models.location

import com.example.horizon.data.remote.location.models.SuggestionsResponse

/**
 * Represents a single suggestion for location auto-fill functionality.
 *
 * @property idOfLocation The unique identifier of the location.
 * @property nameOfLocation The display name of the location.
 * @property addressOfLocation The formatted address, typically "State, Country".
 * @property coordinatesOfLocation The [Coordinates] of the location.
 * @property countryCode The country code of the location.
 */
data class LocationAutofillSuggestion(
    val idOfLocation: String,
    val nameOfLocation: String,
    val addressOfLocation: String,
    val coordinatesOfLocation: Coordinates,
    val countryCode: String
)

/**
 * Converts a list of [SuggestionsResponse.Suggestion] into a list of [LocationAutofillSuggestion].
 *
 * This function filters out any suggestions missing essential fields:
 * - `state`
 * - `country`
 * - `countryCode`
 *
 * @return A list of [LocationAutofillSuggestion] objects derived from valid suggestions.
 */
fun List<SuggestionsResponse.Suggestion>.toLocationAutofillSuggestionList(): List<LocationAutofillSuggestion> =
    filter { it.state != null && it.country != null && it.countryCode != null }
        .map {
            LocationAutofillSuggestion(
                idOfLocation = it.idOfPlace,
                nameOfLocation = it.nameOfPlace,
                addressOfLocation = "${it.state}, ${it.country}",
                coordinatesOfLocation = Coordinates(
                    latitude = it.latitude,
                    longitude = it.longitude
                ),
                countryCode = it.countryCode!!
            )
        }
