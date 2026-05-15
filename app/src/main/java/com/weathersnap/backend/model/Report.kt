package com.weathersnap.backend.model

/**
 * Domain model representing a saved weather report.
 * Used by the UI layer — maps from/to ReportEntity.
 */
data class Report(
    val id: Long = 0,
    val cityName: String,
    val country: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double,
    val imagePath: String,
    val originalImageSize: Long,
    val compressedImageSize: Long,
    val notes: String,
    val timestamp: Long
)
