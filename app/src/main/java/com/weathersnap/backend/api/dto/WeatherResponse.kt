package com.weathersnap.backend.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Response from the Open-Meteo Forecast API.
 * Contains current weather data for a given location.
 */
data class WeatherResponse(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("current") val current: CurrentWeatherDto
)

/**
 * Current weather measurements from the API.
 */
data class CurrentWeatherDto(
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("surface_pressure") val pressure: Double,
    @SerializedName("wind_speed_10m") val windSpeed: Double
)
