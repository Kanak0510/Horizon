package com.project.horizon.di

import com.project.horizon.data.remote.location.HorizonReverseGeocoder
import com.project.horizon.data.remote.location.ReverseGeocoder
import com.project.horizon.domain.location.CurrentLocationProvider
import com.project.horizon.domain.location.HorizonCurrentLocationProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Dagger Hilt module for providing location-related services within the ViewModel scope.
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class LocationServicesModule {

    /**
     * Binds [HorizonCurrentLocationProvider] as the implementation of [CurrentLocationProvider].
     *
     * @param impl The implementation to bind.
     * @return The bound [CurrentLocationProvider] instance.
     */
    @Binds
    abstract fun bindCurrentLocationProvider(
        impl: HorizonCurrentLocationProvider
    ): CurrentLocationProvider

    /**
     * Binds [HorizonReverseGeocoder] as the implementation of [ReverseGeocoder].
     *
     * @param impl The implementation to bind.
     * @return The bound [ReverseGeocoder] instance.
     */
    @Binds
    abstract fun bindReverseGeocoder(
        impl: HorizonReverseGeocoder
    ): ReverseGeocoder
}
