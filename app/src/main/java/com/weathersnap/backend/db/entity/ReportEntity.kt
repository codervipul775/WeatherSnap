package com.weathersnap.backend.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a saved weather report.
 * Stores all weather data captured at report creation time,
 * along with the compressed image path and user notes.
 */
@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
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
