package com.weathersnap.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.backend.model.Report
import com.weathersnap.backend.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Saved Reports screen.
 * Observes all reports from Room via Flow.
 */
@HiltViewModel
class SavedReportsViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        observeReports()
    }

    private fun observeReports() {
        viewModelScope.launch {
            reportRepository.getAllReports().collect { reportList ->
                _reports.value = reportList
                _isLoading.value = false
            }
        }
    }
}
