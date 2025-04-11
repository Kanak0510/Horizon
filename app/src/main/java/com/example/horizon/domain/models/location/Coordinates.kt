package com.example.horizon.domain.models.location

/**
 * Represents geographic coordinates of a location.
 *
 * @property latitude The latitude component, as a [String].
 * @property longitude The longitude component, as a [String].
 */
data class Coordinates(
    val latitude: String,
    val longitude: String
)
