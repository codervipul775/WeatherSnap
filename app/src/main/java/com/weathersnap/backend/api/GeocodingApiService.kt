package com.weathersnap.backend.api

import com.weathersnap.backend.api.dto.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service for Open-Meteo Geocoding API.
 * Searches city suggestions by name — no API key required.
 */
interface GeocodingApiService {

    @GET("v1/search")
    suspend fun searchCities(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): GeocodingResponse
}
