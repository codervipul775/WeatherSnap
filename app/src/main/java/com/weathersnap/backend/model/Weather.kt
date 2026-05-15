package com.weathersnap.backend.model

/**
 * Domain model representing current weather data for a city.
 */
data class Weather(
    val cityName: String,
    val country: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double
)
