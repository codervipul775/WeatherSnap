package com.weathersnap.backend.repository

import com.weathersnap.backend.api.GeocodingApiService
import com.weathersnap.backend.api.WeatherApiService
import com.weathersnap.backend.model.City
import com.weathersnap.backend.model.Weather
import com.weathersnap.backend.util.WeatherCodeMapper
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for weather-related data operations.
 * Handles city search with in-memory caching and weather fetching.
 */
@Singleton
class WeatherRepository @Inject constructor(
    private val geocodingApi: GeocodingApiService,
    private val weatherApi: WeatherApiService
) {

    // In-memory cache to avoid repeated API calls for the same query
    private val cityCache = mutableMapOf<String, List<City>>()

    /**
     * Searches for cities matching the query.
     * Returns cached results if available; otherwise fetches from the API.
     */
    suspend fun searchCities(query: String): List<City> {
        val normalizedQuery = query.trim().lowercase()

        // Return cached result if available
        cityCache[normalizedQuery]?.let { return it }

        // Fetch from API
        val response = geocodingApi.searchCities(name = query)
        val cities = response.results?.map { dto ->
            City(
                id = dto.id,
                name = dto.name,
                latitude = dto.latitude,
                longitude = dto.longitude,
                country = dto.country ?: "Unknown",
                admin1 = dto.admin1
            )
        } ?: emptyList()

        // Cache the result
        cityCache[normalizedQuery] = cities
        return cities
    }

    /**
     * Fetches current weather for a given city using its coordinates.
     */
    suspend fun getWeather(city: City): Weather {
        val response = weatherApi.getCurrentWeather(
            latitude = city.latitude,
            longitude = city.longitude
        )
        val current = response.current

        return Weather(
            cityName = city.name,
            country = city.country,
            temperature = current.temperature,
            condition = WeatherCodeMapper.getCondition(current.weatherCode),
            humidity = current.humidity,
            windSpeed = current.windSpeed,
            pressure = current.pressure
        )
    }
}
