package com.example.horizon.ui.activities

import android.Manifest
import android.location.Location
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

/**
 * Requests both [Manifest.permission.ACCESS_COARSE_LOCATION] and
 * [Manifest.permission.ACCESS_FINE_LOCATION] permissions.
 *
 * @return true if at least one of the permissions is granted, false otherwise.
 */
suspend fun ComponentActivity.requestLocationPermission(): Boolean =
    suspendCancellableCoroutine { continuation ->
        val permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val granted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                continuation.resume(granted)
            }

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

/**
 * Retrieves the current [Location] of the user using [FusedLocationProviderClient].
 *
 * @return the current [Location] if successful, or `null` if an error occurs.
 *
 * @throws CancellationException if the coroutine is cancelled during execution.
 */
@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
suspend fun ComponentActivity.getCurrentLocation(): Location? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    val locationRequest = CurrentLocationRequest.Builder()
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .build()

    return try {
        fusedLocationClient.getCurrentLocation(locationRequest, null).await()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }
}
