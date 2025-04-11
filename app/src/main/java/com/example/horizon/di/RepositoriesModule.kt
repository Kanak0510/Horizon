package com.example.horizon.di

import com.example.horizon.data.repositories.location.HorizonLocationServicesRepository
import com.example.horizon.data.repositories.location.LocationServicesRepository
import com.example.horizon.data.repositories.textgenerator.GenerativeTextRepository
import com.example.horizon.data.repositories.textgenerator.HorizonGenerativeTextRepository
import com.example.horizon.data.repositories.weather.HorizonWeatherRepository
import com.example.horizon.data.repositories.weather.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Hilt module to bind repository interfaces to their concrete implementations.
 * This module is installed in the [ViewModelComponent], meaning these dependencies
 * will be scoped to the lifecycle of a ViewModel.
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoriesModule {

    /**
     * Binds [HorizonLocationServicesRepository] to [LocationServicesRepository].
     */
    @Binds
    abstract fun bindLocationServicesRepository(
        impl: HorizonLocationServicesRepository
    ): LocationServicesRepository

    /**
     * Binds [HorizonWeatherRepository] to [WeatherRepository].
     */
    @Binds
    abstract fun bindWeatherRepository(
        impl: HorizonWeatherRepository
    ): WeatherRepository

    /**
     * Binds [HorizonGenerativeTextRepository] to [GenerativeTextRepository].
     */
    @Binds
    abstract fun bindGenerativeTextRepository(
        impl: HorizonGenerativeTextRepository
    ): GenerativeTextRepository
}
