package com.example.horizon.di

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.horizon.BuildConfig
import com.example.horizon.data.workers.CleanupWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Application class for the Horizon app. Sets up Hilt, Timber, and periodic cleanup work.
 */
@HiltAndroidApp
class Horizon : Application(), Configuration.Provider {

    /**
     * Injected [HiltWorkerFactory] used to provide custom Worker instances.
     */
    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for logging in debug builds.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Schedule periodic cleanup task to delete marked items from the database.
        enqueueDeleteMarkedItemsWorker()
    }

    /**
     * Configures WorkManager to use Hilt for Worker dependency injection.
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()

    /**
     * Enqueues a [CleanupWorker] to run weekly, cleaning up deleted or unused database entries.
     */
    private fun enqueueDeleteMarkedItemsWorker() {
        val periodicWorkRequest = PeriodicWorkRequestBuilder<CleanupWorker>(
            7, TimeUnit.DAYS // Repeat every 7 days
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DELETE_MARKED_ITEMS_WORK_ID,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    companion object {
        private const val DELETE_MARKED_ITEMS_WORK_ID =
            "com.example.horizon.data.workers.CleanupWorker"
    }
}
