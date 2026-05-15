package com.weathersnap.backend.api

import com.weathersnap.backend.api.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service for Open-Meteo Forecast API.
 * Fetches current weather data for a given latitude/longitude.
 */
interface WeatherApiService {

    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,weather_code,surface_pressure,wind_speed_10m",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}
