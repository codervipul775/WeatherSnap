package com.weathersnap.backend.model

/**
 * Domain model representing a city from geocoding search.
 */
data class City(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val admin1: String?
) {
    /** Display name shown in city suggestions list. */
    val displayName: String
        get() = if (admin1 != null) "$name, $country" else "$name, $country"
}
