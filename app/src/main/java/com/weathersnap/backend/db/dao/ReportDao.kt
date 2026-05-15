package com.weathersnap.backend.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.weathersnap.backend.db.entity.ReportEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for weather reports.
 * All queries run on IO threads via Room's built-in coroutine/Flow support.
 */
@Dao
interface ReportDao {

    /** Observe all saved reports ordered by most recent first. */
    @Query("SELECT * FROM reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    /** Insert a new report into the database. */
    @Insert
    suspend fun insertReport(report: ReportEntity)
}
