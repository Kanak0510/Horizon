package com.example.horizon.domain.location

import android.content.Context
import androidx.annotation.RequiresPermission
import com.example.horizon.domain.models.location.Coordinates
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementation of [CurrentLocationProvider] that uses Google's [FusedLocationProviderClient]
 * to fetch the current location of the device.
 *
 * @property context Application context used to access location services.
 */
class HorizonCurrentLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : CurrentLocationProvider {

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    /**
     * Asynchronously retrieves the current location of the device.
     *
     * Requires either [android.Manifest.permission.ACCESS_COARSE_LOCATION] or
     * [android.Manifest.permission.ACCESS_FINE_LOCATION].
     *
     * @return A [Result] containing the [Coordinates] if successful, or a failure if an exception occurs.
     */
    @RequiresPermission(
        anyOf = [android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION]
    )
    override suspend fun getCurrentLocation(): Result<Coordinates> = try {
        val currentLocationRequest = CurrentLocationRequest.Builder()
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        val location = fusedLocationProviderClient
            .getCurrentLocation(currentLocationRequest, null)
            .await()

        val coordinates = Coordinates(
            latitude = location.latitude.toString(),
            longitude = location.longitude.toString()
        )

        Result.success(coordinates)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }
}
