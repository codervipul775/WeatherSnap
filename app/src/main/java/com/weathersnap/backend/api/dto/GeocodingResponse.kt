package com.weathersnap.backend.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Response from the Open-Meteo Geocoding API.
 * Contains a list of city results matching the search query.
 */
data class GeocodingResponse(
    @SerializedName("results") val results: List<CityResult>? = null
)

/**
 * A single city result from the geocoding search.
 */
data class CityResult(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("country") val country: String? = null,
    @SerializedName("admin1") val admin1: String? = null
)
