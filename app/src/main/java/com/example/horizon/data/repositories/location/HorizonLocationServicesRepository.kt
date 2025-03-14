package com.example.horizon.data.repositories.location

import com.example.horizon.BuildConfig
import com.example.horizon.data.remote.location.LocationClient
import com.example.horizon.data.remote.location.models.coordinates
import com.example.horizon.data.remote.location.models.toLocationAutofillSuggestionList
import com.example.horizon.domain.models.LocationAutofillSuggestion
import java.util.UUID
import javax.inject.Inject

/**
 * A concrete implementation of [LocationServicesRepository].
 */
class HorizonLocationServicesRepository @Inject constructor(
    private val locationClient: LocationClient
) : LocationServicesRepository {

    override suspend fun fetchSuggestedPlacesForQuery(query: String): Result<List<LocationAutofillSuggestion>> =
        runCatching {
            val sessionToken = UUID.randomUUID().toString()
            val suggestionCoordinatePairs = locationClient.getPlacesSuggestionsForQuery(
                query = query,
                accessToken = BuildConfig.MAP_BOX_API_KEY,
                sessionToken = sessionToken,
            ).body()!!.suggestions.associateWith { suggestion ->
                locationClient.getCoordinatesForPlace(
                    placeId = suggestion.idOfPlace,
                    accessToken = BuildConfig.MAP_BOX_API_KEY,
                    sessionToken = sessionToken
                ).body()!!.coordinates
            }.toList()
            suggestionCoordinatePairs.map { (suggestion, coordinates) ->
                val autoFillSuggestionCoordinate = LocationAutofillSuggestion.Coordinates(
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude
                )
                suggestion.toLocationAutofillSuggestionList(autoFillSuggestionCoordinate)
            }
        }
}