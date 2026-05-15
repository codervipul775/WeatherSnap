package com.weathersnap.backend.repository

import com.weathersnap.backend.db.dao.ReportDao
import com.weathersnap.backend.db.entity.ReportEntity
import com.weathersnap.backend.model.Report
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for report data operations.
 * Bridges the domain layer and Room database.
 */
@Singleton
class ReportRepository @Inject constructor(
    private val reportDao: ReportDao
) {

    /** Observes all saved reports as a Flow of domain models. */
    fun getAllReports(): Flow<List<Report>> {
        return reportDao.getAllReports().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /** Saves a new report to the database. */
    suspend fun saveReport(report: Report) {
        reportDao.insertReport(report.toEntity())
    }

    // ── Mappers ──

    private fun ReportEntity.toDomain(): Report {
        return Report(
            id = id,
            cityName = cityName,
            country = country,
            temperature = temperature,
            condition = condition,
            humidity = humidity,
            windSpeed = windSpeed,
            pressure = pressure,
            imagePath = imagePath,
            originalImageSize = originalImageSize,
            compressedImageSize = compressedImageSize,
            notes = notes,
            timestamp = timestamp
        )
    }

    private fun Report.toEntity(): ReportEntity {
        return ReportEntity(
            id = id,
            cityName = cityName,
            country = country,
            temperature = temperature,
            condition = condition,
            humidity = humidity,
            windSpeed = windSpeed,
            pressure = pressure,
            imagePath = imagePath,
            originalImageSize = originalImageSize,
            compressedImageSize = compressedImageSize,
            notes = notes,
            timestamp = timestamp
        )
    }
}
