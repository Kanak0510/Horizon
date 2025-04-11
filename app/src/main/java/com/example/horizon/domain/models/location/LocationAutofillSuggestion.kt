package com.example.horizon.domain.models.location

import com.example.horizon.data.remote.location.models.SuggestionsResponse
import com.example.horizon.data.remote.location.models.circularCountryFlagUrl

/**
 * Represents a single suggestion for location auto-fill functionality.
 *
 * @property idOfLocation The unique identifier of the location.
 * @property nameOfLocation The display name of the location.
 * @property addressOfLocation The formatted address, typically "State, Country".
 * @property coordinatesOfLocation The [Coordinates] of the location.
 * @property countryFlagUrl A URL pointing to a circular version of the country's flag.
 */
data class LocationAutofillSuggestion(
    val idOfLocation: String,
    val nameOfLocation: String,
    val addressOfLocation: String,
    val coordinatesOfLocation: Coordinates,
    val countryFlagUrl: String
)

/**
 * Converts a list of [SuggestionsResponse.Suggestion] into a list of [LocationAutofillSuggestion].
 *
 * This function filters out any suggestions missing essential fields:
 * - `state`
 * - `country`
 * - `circularCountryFlagUrl`
 *
 * @return A list of [LocationAutofillSuggestion] objects derived from valid suggestions.
 */
fun List<SuggestionsResponse.Suggestion>.toLocationAutofillSuggestionList(): List<LocationAutofillSuggestion> =
    filter { it.state != null && it.country != null && it.circularCountryFlagUrl != null }
        .map {
            LocationAutofillSuggestion(
                idOfLocation = it.idOfPlace,
                nameOfLocation = it.nameOfPlace,
                addressOfLocation = "${it.state}, ${it.country}",
                coordinatesOfLocation = Coordinates(
                    latitude = it.latitude,
                    longitude = it.longitude
                ),
                countryFlagUrl = it.circularCountryFlagUrl!!
            )
        }
