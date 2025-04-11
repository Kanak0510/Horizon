package com.example.horizon.data.remote.location.models

import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class SuggestionsResponseTest {

    @Test
    fun `test SuggestionsResponse deserialization and local flag path`() {
        // Sample JSON you would get from the API
        val json = """
            {
              "results": [
                {
                  "id": "2950159",
                  "name": "Berlin",
                  "country": "Deutschland",
                  "admin1": "Berlin",
                  "country_code": "DE",
                  "latitude": "52.52437",
                  "longitude": "13.41053"
                }
              ]
            }
        """

        val suggestionsResponse = Json.decodeFromString(SuggestionsResponse.serializer(), json)

        assertEquals(1, suggestionsResponse.suggestions.size)

        val suggestion = suggestionsResponse.suggestions[0]

        assertEquals("2950159", suggestion.idOfPlace)
        assertEquals("Berlin", suggestion.nameOfPlace)
        assertEquals("Deutschland", suggestion.country)
        assertEquals("DE", suggestion.countryCode)
        assertEquals("52.52437", suggestion.latitude)
        assertEquals("13.41053", suggestion.longitude)
        assertEquals("file:///android_asset/flags/de.svg", suggestion.localFlagAssetPath)
    }
}
