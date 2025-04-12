package com.project.horizon.data.repositories.location

import com.project.horizon.domain.models.location.LocationAutofillSuggestion

/**
 * Repository interface responsible for managing all location-related operations,
 * such as fetching place suggestions based on user queries.
 */
interface LocationServicesRepository {

    /**
     * Fetches a list of suggested locations based on the provided [query].
     *
     * @param query The user input string used to search for matching place suggestions.
     * @return A [Result] containing a list of [LocationAutofillSuggestion] if the operation
     * was successful, or an error if the request failed.
     */
    suspend fun fetchSuggestedPlacesForQuery(query: String): Result<List<LocationAutofillSuggestion>>
}
