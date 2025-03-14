package com.example.horizon.data.remote.location

import com.example.horizon.BuildConfig
import com.example.horizon.di.NetworkModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocationClientTest {

    private val locationClient = NetworkModule.provideLocationClient()

    @Test
    fun `Get Suggested places for a valid query`() = runTest {
        val response = locationClient.getPlacesSuggestionsForQuery(
            "Apple Park",
            BuildConfig.MAP_BOX_API_KEY,
            "session_token"
        )
        advanceUntilIdle()
        assert(response.isSuccessful)
        assert(response.body()!!.suggestions.isNotEmpty())
    }

    @Test
    fun `Get empty list of suggested places for an invalid query`() = runTest {
        val response = locationClient.getPlacesSuggestionsForQuery(
            "123jnfdjijnfijnijndsf",
            BuildConfig.MAP_BOX_API_KEY,
            "session_token"
        )
        advanceUntilIdle()
        assert(response.isSuccessful)
        assert(response.body()!!.suggestions.isEmpty())
    }
}