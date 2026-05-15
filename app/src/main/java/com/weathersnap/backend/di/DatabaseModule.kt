package com.weathersnap.backend.di

import android.content.Context
import androidx.room.Room
import com.weathersnap.backend.db.WeatherSnapDatabase
import com.weathersnap.backend.db.dao.ReportDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing Room database dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WeatherSnapDatabase {
        return Room.databaseBuilder(
            context,
            WeatherSnapDatabase::class.java,
            "weathersnap_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideReportDao(database: WeatherSnapDatabase): ReportDao {
        return database.reportDao()
    }
}
