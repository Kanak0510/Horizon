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
 * [CleanupWorker] is responsible for performing background cleanup operations
 * in the database, such as removing soft-deleted weather entries and clearing
 * cached generated texts.
 *
 * This worker ensures that unnecessary or obsolete data is removed periodically
 * to maintain app performance and data relevance.
 *
 * @property savedWeatherDetailsDao DAO to interact with saved weather data.
 * @property generatedTextCacheDao DAO to interact with cached text generations.
 * @property ioDispatcher Dispatcher for performing I/O-bound operations.
 */
@HiltWorker
class CleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val savedWeatherDetailsDao: HorizonDatabaseDao,
    private val generatedTextCacheDao: GeneratedTextCacheDatabaseDao,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(context, workerParameters) {

    /**
     * Executes the cleanup tasks on the I/O dispatcher.
     *
     * @return [Result.success] if cleanup was successful,
     *         [Result.failure] if an error occurred (excluding cancellation).
     */
    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        return@withContext try {
            savedWeatherDetailsDao.deleteAllItemsMarkedAsDeleted()
            generatedTextCacheDao.deleteAllSavedText()
            Result.success()
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure()
        }
    }
}
