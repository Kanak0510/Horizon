package com.project.horizon.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

/**
 * Qualifier for marking the Main dispatcher.
 */
@Qualifier
annotation class MainDispatcher

/**
 * Qualifier for marking the IO dispatcher.
 */
@Qualifier
annotation class IODispatcher

/**
 * Qualifier for marking the Default dispatcher.
 */
@Qualifier
annotation class DefaultDispatcher

/**
 * Hilt module that provides different Coroutine dispatchers as dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoroutineDispatchersModule {

    /**
     * Provides the [Dispatchers.Default] coroutine dispatcher.
     */
    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    /**
     * Provides the [Dispatchers.IO] coroutine dispatcher.
     */
    @Provides
    @IODispatcher
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides the [Dispatchers.Main] coroutine dispatcher.
     */
    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
