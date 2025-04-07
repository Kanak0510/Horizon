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

class HorizonReverseGeocoder @Inject constructor(
    @ApplicationContext private val context: Context,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ReverseGeocoder {

    override suspend fun getLocationNameForCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<String> = withContext(ioDispatcher) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())

            val address: Address? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
            }

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
