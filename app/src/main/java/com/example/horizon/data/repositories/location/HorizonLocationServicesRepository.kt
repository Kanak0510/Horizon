package com.example.horizon.data.repositories.location

import com.example.horizon.data.getBodyOrThrowException
import com.example.horizon.data.remote.location.LocationClient
import com.example.horizon.domain.models.location.LocationAutofillSuggestion
import com.example.horizon.domain.models.location.toLocationAutofillSuggestionList
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * Concrete implementation of [LocationServicesRepository] that interacts with a remote
 * location client to provide location-based suggestions.
 *
 * @property locationClient The client responsible for communicating with the location API.
 */
class HorizonLocationServicesRepository @Inject constructor(
    private val locationClient: LocationClient
) : LocationServicesRepository {

    /**
     * Fetches a list of location autofill suggestions based on a user-entered query.
     *
     * @param query The user's input text used to search for location suggestions.
     * @return A [Result] containing a list of [LocationAutofillSuggestion] on success,
     * or an error on failure.
     *
     * - If the query is blank, an empty list is returned immediately.
     * - If an exception occurs (excluding [CancellationException]), the error is wrapped in a [Result.failure].
     */
    override suspend fun fetchSuggestedPlacesForQuery(query: String): Result<List<LocationAutofillSuggestion>> {
        return try {
            if (query.isBlank()) return Result.success(emptyList())

            val suggestions = locationClient.getPlacesSuggestionsForQuery(query = query)
                .getBodyOrThrowException()
                .suggestions
                .toLocationAutofillSuggestionList()

            Result.success(suggestions)
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }
}
