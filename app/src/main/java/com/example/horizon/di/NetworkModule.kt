package com.example.horizon.di

import com.example.horizon.BuildConfig
import com.example.horizon.data.remote.languagemodel.GeminiTextGeneratorClient
import com.example.horizon.data.remote.languagemodel.TextGeneratorClient
import com.example.horizon.data.remote.languagemodel.TextGeneratorClientConstants
import com.example.horizon.data.remote.location.LocationClient
import com.example.horizon.data.remote.location.LocationClientConstants
import com.example.horizon.data.remote.weather.WeatherClient
import com.example.horizon.data.remote.weather.WeatherClientConstants
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualifier annotation for the OpenAI-based [TextGeneratorClient].
 */
@Qualifier
annotation class OpenAIClient

/**
 * Qualifier annotation for the Gemini-based [TextGeneratorClient].
 */
@Qualifier
annotation class GeminiClient

/**
 * Provides singleton-scoped network dependencies such as Retrofit clients and API interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val contentType = "application/json".toMediaType()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    /**
     * Provides the [WeatherClient] for making weather-related API calls.
     */
    @Provides
    @Singleton
    fun provideWeatherClient(): WeatherClient = Retrofit.Builder()
        .baseUrl(WeatherClientConstants.BASE_URL)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
        .create(WeatherClient::class.java)

    /**
     * Provides the [LocationClient] for accessing location APIs.
     */
    @Provides
    @Singleton
    fun provideLocationClient(): LocationClient = Retrofit.Builder()
        .baseUrl(LocationClientConstants.BASE_URL)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
        .create(LocationClient::class.java)

    /**
     * Provides an OpenAI-powered [TextGeneratorClient] for text generation features.
     * Includes an authorization interceptor with the API token.
     */
    @Provides
    @Singleton
    @OpenAIClient
    fun provideOpenAITextGeneratorClient(): TextGeneratorClient = Retrofit.Builder()
        .client(
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${BuildConfig.OPEN_AI_API_TOKEN}")
                        .build()
                    chain.proceed(newRequest)
                }
                .build()
        )
        .baseUrl(TextGeneratorClientConstants.BASE_URL)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
        .create(TextGeneratorClient::class.java)

    /**
     * Provides a Gemini-based implementation of [TextGeneratorClient].
     * This client does not use Retrofit but wraps custom logic internally.
     */
    @Provides
    @Singleton
    @GeminiClient
    fun provideGeminiTextGeneratorClient(): TextGeneratorClient = GeminiTextGeneratorClient()
}
