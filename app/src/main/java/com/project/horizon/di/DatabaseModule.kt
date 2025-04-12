package com.project.horizon.di

import android.content.Context
import androidx.room.Room
import com.project.horizon.data.local.textgeneration.GeneratedTextCacheDatabaseDao
import com.project.horizon.data.local.textgeneration.HorizonGeneratedTextCacheDatabase
import com.project.horizon.data.local.weather.HorizonDatabase
import com.project.horizon.data.local.weather.HorizonDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module responsible for providing database-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides a singleton instance of [HorizonDatabaseDao], backed by [HorizonDatabase].
     *
     * @param context Application context injected by Hilt.
     * @return The DAO interface for weather-related database operations.
     */
    @Provides
    @Singleton
    fun provideWeatherDatabaseDao(
        @ApplicationContext context: Context
    ): HorizonDatabaseDao {
        return Room.databaseBuilder(
            context,
            HorizonDatabase::class.java,
            HorizonDatabase.DATABASE_NAME
        ).build().getDao()
    }

    /**
     * Provides a singleton instance of [GeneratedTextCacheDatabaseDao], backed by
     * [HorizonGeneratedTextCacheDatabase].
     *
     * @param context Application context injected by Hilt.
     * @return The DAO interface for generated text cache operations.
     */
    @Provides
    @Singleton
    fun provideGeneratedTextCacheDatabaseDao(
        @ApplicationContext context: Context
    ): GeneratedTextCacheDatabaseDao {
        return Room.databaseBuilder(
            context,
            HorizonGeneratedTextCacheDatabase::class.java,
            HorizonGeneratedTextCacheDatabase.DATABASE_NAME
        ).build().getDao()
    }
}
