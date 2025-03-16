package com.example.horizon.data.repositories.location

import com.example.horizon.data.getBodyOrThrowException
import com.example.horizon.data.remote.location.LocationClient
import com.example.horizon.data.remote.location.models.coordinates
import com.example.horizon.data.remote.location.models.toLocationAutofillSuggestionList
import com.example.horizon.domain.models.LocationAutofillSuggestion
import java.util.UUID
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

            val commonSessionToken = UUID.randomUUID().toString()

            val suggestions = locationClient.getPlacesSuggestionsForQuery(
                query = query,
                sessionToken = commonSessionToken,
            ).getBodyOrThrowException().suggestions

            // { suggestion -> coordinate }
            val suggestionCoordinateMap = suggestions.associateWith { suggestion ->
                locationClient.getCoordinatesForPlace(
                    placeId = suggestion.idOfPlace,
                    sessionToken = commonSessionToken
                ).getBodyOrThrowException().coordinates
            }

            val autofillSuggestions = suggestionCoordinateMap.map { (suggestion, coordinates) ->
                val autoFillSuggestionCoordinate = LocationAutofillSuggestion.Coordinates(
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude
                )
                suggestion.toLocationAutofillSuggestionList(autoFillSuggestionCoordinate)
            }
            Result.success(autofillSuggestions)
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }
}