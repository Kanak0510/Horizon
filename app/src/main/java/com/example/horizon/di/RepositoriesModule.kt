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

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoriesModule {

    @Binds
    abstract fun bindLocationServicesRepository(
        impl: HorizonLocationServicesRepository
    ): LocationServicesRepository

    @Binds
    abstract fun bindWeatherRepository(
        impl: HorizonWeatherRepository
    ): WeatherRepository

    @Binds
    abstract fun bindGenerativeTextRepository(
        impl: HorizonGenerativeTextRepository
    ): GenerativeTextRepository
}