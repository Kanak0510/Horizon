package com.example.horizon.di

import com.example.horizon.domain.location.CurrentLocationProvider
import com.example.horizon.domain.location.HorizonCurrentLocationProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class LocationServicesModule {

    @Binds
    abstract fun bindCurrentLocationProvider(impl: HorizonCurrentLocationProvider): CurrentLocationProvider
}