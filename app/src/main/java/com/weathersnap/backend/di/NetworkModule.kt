package com.weathersnap.backend.di

import com.weathersnap.backend.api.GeocodingApiService
import com.weathersnap.backend.api.WeatherApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton
import com.weathersnap.BuildConfig

/**
 * Hilt module providing network-related dependencies.
 * Logging interceptor is enabled only in debug builds.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/"
    private const val WEATHER_BASE_URL = "https://api.open-meteo.com/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        // Debug-only network logging
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    @Named("geocoding")
    fun provideGeocodingRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GEOCODING_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("weather")
    fun provideWeatherRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGeocodingApiService(@Named("geocoding") retrofit: Retrofit): GeocodingApiService {
        return retrofit.create(GeocodingApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(@Named("weather") retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }
}
