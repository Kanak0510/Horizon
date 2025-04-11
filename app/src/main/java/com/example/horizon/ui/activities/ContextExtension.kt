package com.example.horizon.ui.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Checks whether the app has either coarse or fine location permission.
 *
 * @return `true` if either [Manifest.permission.ACCESS_COARSE_LOCATION] or
 * [Manifest.permission.ACCESS_FINE_LOCATION] is granted, `false` otherwise.
 */
fun Context.hasLocationPermission(): Boolean {
    val coarseGranted = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val fineGranted = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return coarseGranted || fineGranted
}
