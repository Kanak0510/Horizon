package com.project.horizon.domain.location

import com.project.horizon.domain.models.location.Coordinates

/**
 * Interface for providing the current location of the device.
 *
 * Implementations of this interface are expected to use platform-specific
 * location services to retrieve the current location as [Coordinates].
 */
fun interface CurrentLocationProvider {

    /**
     * Asynchronously retrieves the current location of the device.
     *
     * @return A [Result] containing either the current [Coordinates] if successful,
     * or an exception in case of failure.
     */
    suspend fun getCurrentLocation(): Result<Coordinates>
}
