package com.example.horizon.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.horizon.data.local.textgeneration.GeneratedTextCacheDatabaseDao
import com.example.horizon.data.local.weather.HorizonDatabaseDao
import com.example.horizon.di.IODispatcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * A worker that deletes all non-required items in the database.
 */
@HiltWorker
class CleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val savedWeatherDetailsDao: HorizonDatabaseDao,
    private val generatedTextCacheDao: GeneratedTextCacheDatabaseDao,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        try {
            savedWeatherDetailsDao.deleteAllItemsMarkedAsDeleted()
            generatedTextCacheDao.deleteAllSavedText()
            Result.success()
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure()
        }
    }
}