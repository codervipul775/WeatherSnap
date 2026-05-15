package com.weathersnap.backend.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weathersnap.backend.db.dao.ReportDao
import com.weathersnap.backend.db.entity.ReportEntity

/**
 * Room database for the WeatherSnap app.
 * Single table: reports.
 */
@Database(
    entities = [ReportEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherSnapDatabase : RoomDatabase() {
    abstract fun reportDao(): ReportDao
}
