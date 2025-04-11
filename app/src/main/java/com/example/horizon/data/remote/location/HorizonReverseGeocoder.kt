package com.example.horizon.data.remote.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Build
import com.example.horizon.di.IODispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * A reverse geocoder that resolves a location name from given geographic coordinates.
 *
 * This implementation uses Android's [Geocoder] API to fetch human-readable address information
 * for a given pair of [latitude] and [longitude]. It handles both modern and legacy API levels,
 * adapting to asynchronous and synchronous methods accordingly.
 *
 * @constructor Creates an instance of [HorizonReverseGeocoder] with dependency-injected context and dispatcher.
 *
 * @param context The application context, injected via Hilt.
 * @param ioDispatcher The coroutine dispatcher to run geocoding on the I/O thread.
 */
class HorizonReverseGeocoder @Inject constructor(
    @ApplicationContext private val context: Context,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ReverseGeocoder {

    /**
     * Resolves a user-friendly location name (typically in the format "City, State") for
     * the provided geographic coordinates.
     *
     * @param latitude The latitude value of the location.
     * @param longitude The longitude value of the location.
     * @return A [Result] wrapping the location name on success or an exception on failure.
     */
    override suspend fun getLocationNameForCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<String> = withContext(ioDispatcher) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())

            val address: Address? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For API 33+ use async geocoding
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1,
                        object : GeocodeListener {
                            override fun onGeocode(addresses: MutableList<Address>) {
                                if (addresses.isNotEmpty()) {
                                    continuation.resume(addresses.first())
                                } else {
                                    continuation.resumeWithException(Exception("No address found"))
                                }
                            }

                            override fun onError(errorMessage: String?) {
                                continuation.resumeWithException(Exception(errorMessage ?: "Geocoding failed"))
                            }
                        }
                    )
                }
            } else {
                // For API < 33, use the synchronous (deprecated) version
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
            }

            // Format address if available
            if (address != null) {
                Result.success("${address.locality}, ${address.adminArea}")
            } else {
                Result.failure(Exception("No address found"))
            }

        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }
}
