package com.project.horizon.data.remote.location

import androidx.annotation.IntRange
import com.project.horizon.data.remote.location.models.SuggestionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * A Retrofit interface representing a network client that provides location-based services,
 * such as returning place suggestions based on a given query string.
 */
interface LocationClient {

    /**
     * Retrieves a list of place suggestions that match the specified [query].
     *
     * This endpoint is typically used to power autocomplete or search features for locations.
     *
     * @param query The search query for which place suggestions are to be fetched.
     * @param count The maximum number of suggestions to return. Must be between 1 and 100. Defaults to 20.
     * @return A [Response] object wrapping a [SuggestionsResponse] containing the suggested places.
     */
    @GET(LocationClientConstants.EndPoints.GET_PLACES_SUGGESTIONS_FOR_QUERY)
    suspend fun getPlacesSuggestionsForQuery(
        @Query("name") query: String,
        @Query("count") @IntRange(1, 100) count: Int = 20
    ): Response<SuggestionsResponse>
}
