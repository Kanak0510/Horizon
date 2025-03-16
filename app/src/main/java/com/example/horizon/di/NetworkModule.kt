package com.example.horizon.di

import com.example.horizon.data.remote.location.LocationClient
import com.example.horizon.data.remote.location.LocationClientConstants
import com.example.horizon.data.remote.weather.WeatherClient
import com.example.horizon.data.remote.weather.WeatherClientConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideWeatherClient(): WeatherClient = Retrofit.Builder()
        .baseUrl(WeatherClientConstants.BASE_URL)
        .client(WeatherClientConstants.AutoAddApiKeyClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(WeatherClient::class.java)

    @Provides
    @Singleton
    fun provideLocationClient(): LocationClient = Retrofit.Builder()
        .baseUrl(LocationClientConstants.BASE_URL)
        .client(LocationClientConstants.AutoAddApiKeyClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(LocationClient::class.java)
}