package com.example.horizon.data.repositories.location

import com.example.horizon.data.getBodyOrThrowException
import com.example.horizon.data.remote.location.LocationClient
import com.example.horizon.data.remote.location.models.toLocationAutofillSuggestionList
import com.example.horizon.domain.models.LocationAutofillSuggestion
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * A concrete implementation of [LocationServicesRepository].
 */
class HorizonLocationServicesRepository @Inject constructor(
    private val locationClient: LocationClient
) : LocationServicesRepository {

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