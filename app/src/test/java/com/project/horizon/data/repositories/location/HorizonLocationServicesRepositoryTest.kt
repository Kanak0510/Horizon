package com.project.horizon.data.repositories.location

import com.project.horizon.di.NetworkModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HorizonLocationServicesRepositoryTest {

    private val repository = HorizonLocationServicesRepository(
        locationClient = NetworkModule.provideLocationClient()
    )

    @Test
    fun `A valid query to fetch suggested places should successfully fetch list of suggestions`() =
        runTest {
            val result = repository.fetchSuggestedPlacesForQuery(query = "GooglePlex")
            advanceUntilIdle()
            assert(result.isSuccess)
        }
}