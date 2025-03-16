package com.example.horizon.di

import android.content.Context
import androidx.room.Room
import com.example.horizon.data.local.weather.HorizonDatabase
import com.example.horizon.data.local.weather.HorizonDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideJustWeatherDatabaseDao(
        @ApplicationContext context: Context
    ): HorizonDatabaseDao = Room.databaseBuilder(
        context = context,
        klass = HorizonDatabase::class.java,
        name = HorizonDatabase.DATABASE_NAME
    ).build().getDao()
}