package com.weathersnap.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.backend.model.Report
import com.weathersnap.backend.model.Weather
import com.weathersnap.backend.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

/**
 * ViewModel for the Create Report screen.
 * Manages weather data, captured image, notes, and save operation.
 */
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // ── Image State (Survives Process Death) ──
    val imagePath: StateFlow<String?> = savedStateHandle.getStateFlow("imagePath", null)
    val originalImageSize: StateFlow<Long> = savedStateHandle.getStateFlow("originalImageSize", 0L)
    val compressedImageSize: StateFlow<Long> = savedStateHandle.getStateFlow("compressedImageSize", 0L)

    // ── Notes (Survives Process Death) ──
    val notes: StateFlow<String> = savedStateHandle.getStateFlow("notes", "")

    // ── Save State ──
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    /** Updates the notes text. */
    fun onNotesChanged(text: String) {
        savedStateHandle["notes"] = text
    }

    /** Sets the captured and compressed image data. */
    fun setImageData(path: String, originalSize: Long, compressedSize: Long) {
        savedStateHandle["imagePath"] = path
        savedStateHandle["originalImageSize"] = originalSize
        savedStateHandle["compressedImageSize"] = compressedSize
    }

    /** Saves the report to Room DB on the IO dispatcher. */
    fun saveReport(weather: Weather) {
        val currentImagePath = imagePath.value
        if (currentImagePath == null) {
            _saveError.value = "Please capture a photo first"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isSaving.value = true
            try {
                // Move file from cache to persistent files dir
                val tempFile = File(currentImagePath)
                val persistentFile = File(context.filesDir, tempFile.name)
                if (tempFile.exists()) {
                    tempFile.copyTo(persistentFile, overwrite = true)
                    tempFile.delete()
                }

                val report = Report(
                    cityName = weather.cityName,
                    country = weather.country,
                    temperature = weather.temperature,
                    condition = weather.condition,
                    humidity = weather.humidity,
                    windSpeed = weather.windSpeed,
                    pressure = weather.pressure,
                    imagePath = persistentFile.absolutePath,
                    originalImageSize = originalImageSize.value,
                    compressedImageSize = compressedImageSize.value,
                    notes = notes.value,
                    timestamp = System.currentTimeMillis()
                )
                reportRepository.saveReport(report)
                _saveSuccess.value = true
            } catch (e: Exception) {
                _saveError.value = e.localizedMessage ?: "Failed to save report"
            } finally {
                _isSaving.value = false
            }
        }
    }

    /** Resets the ViewModel state for a new report. */
    fun resetState() {
        savedStateHandle["imagePath"] = null
        savedStateHandle["originalImageSize"] = 0L
        savedStateHandle["compressedImageSize"] = 0L
        savedStateHandle["notes"] = ""
        _saveSuccess.value = false
        _saveError.value = null
    }
}
