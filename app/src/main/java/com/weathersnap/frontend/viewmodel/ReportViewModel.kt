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
import javax.inject.Inject

/**
 * ViewModel for the Create Report screen.
 * Manages weather data, captured image, notes, and save operation.
 */
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    // ── Image State ──
    private val _imagePath = MutableStateFlow<String?>(null)
    val imagePath: StateFlow<String?> = _imagePath.asStateFlow()

    private val _originalImageSize = MutableStateFlow(0L)
    val originalImageSize: StateFlow<Long> = _originalImageSize.asStateFlow()

    private val _compressedImageSize = MutableStateFlow(0L)
    val compressedImageSize: StateFlow<Long> = _compressedImageSize.asStateFlow()

    // ── Notes ──
    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    // ── Save State ──
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    /** Updates the notes text. */
    fun onNotesChanged(text: String) {
        _notes.value = text
    }

    /** Sets the captured and compressed image data. */
    fun setImageData(path: String, originalSize: Long, compressedSize: Long) {
        _imagePath.value = path
        _originalImageSize.value = originalSize
        _compressedImageSize.value = compressedSize
    }

    /** Saves the report to Room DB on the IO dispatcher. */
    fun saveReport(weather: Weather) {
        val currentImagePath = _imagePath.value
        if (currentImagePath == null) {
            _saveError.value = "Please capture a photo first"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isSaving.value = true
            try {
                val report = Report(
                    cityName = weather.cityName,
                    country = weather.country,
                    temperature = weather.temperature,
                    condition = weather.condition,
                    humidity = weather.humidity,
                    windSpeed = weather.windSpeed,
                    pressure = weather.pressure,
                    imagePath = currentImagePath,
                    originalImageSize = _originalImageSize.value,
                    compressedImageSize = _compressedImageSize.value,
                    notes = _notes.value,
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
        _imagePath.value = null
        _originalImageSize.value = 0L
        _compressedImageSize.value = 0L
        _notes.value = ""
        _saveSuccess.value = false
        _saveError.value = null
    }
}
