package com.weathersnap.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.backend.model.City
import com.weathersnap.backend.model.Weather
import com.weathersnap.backend.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Weather screen.
 */
sealed class WeatherUiState {
    /** Initial state — no search performed yet. */
    object Idle : WeatherUiState()

    /** Loading weather data. */
    object Loading : WeatherUiState()

    /** Successfully fetched weather data. */
    data class Success(val weather: Weather) : WeatherUiState()

    /** An error occurred during fetch. */
    data class Error(val message: String) : WeatherUiState()
}

/**
 * ViewModel for the Weather search screen.
 * Handles city search with debounce + caching and weather fetch.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    // ── Search Query ──
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // ── City Suggestions ──
    private val _citySuggestions = MutableStateFlow<List<City>>(emptyList())
    val citySuggestions: StateFlow<List<City>> = _citySuggestions.asStateFlow()

    // ── Suggestions Loading ──
    private val _isSuggestionsLoading = MutableStateFlow(false)
    val isSuggestionsLoading: StateFlow<Boolean> = _isSuggestionsLoading.asStateFlow()

    // ── Weather State ──
    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    // ── Selected City ──
    private val _selectedCity = MutableStateFlow<City?>(null)
    val selectedCity: StateFlow<City?> = _selectedCity.asStateFlow()

    init {
        // Debounced city search — triggers after 300ms of no typing
        _searchQuery
            .debounce(300)
            .distinctUntilChanged()
            .filter { it.length > 2 }
            .onEach { query -> fetchCitySuggestions(query) }
            .launchIn(viewModelScope)
    }

    /** Called when the user types in the search field. */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _selectedCity.value = null

        // Clear suggestions if query is too short
        if (query.length <= 2) {
            _citySuggestions.value = emptyList()
        }
    }

    /** Called when the user selects a city from the suggestions. */
    fun onCitySelected(city: City) {
        _selectedCity.value = city
        _searchQuery.value = city.displayName
        _citySuggestions.value = emptyList()
        fetchWeather(city)
    }

    /** Called when the user taps the Search button. */
    fun onSearchClicked() {
        val city = _selectedCity.value
        if (city != null) {
            fetchWeather(city)
        } else {
            // If no city selected, try to search suggestions
            val query = _searchQuery.value
            if (query.length > 2) {
                viewModelScope.launch {
                    fetchCitySuggestions(query)
                }
            }
        }
    }

    private fun fetchCitySuggestions(query: String) {
        viewModelScope.launch {
            _isSuggestionsLoading.value = true
            try {
                val cities = weatherRepository.searchCities(query)
                _citySuggestions.value = cities
            } catch (e: Exception) {
                _citySuggestions.value = emptyList()
            } finally {
                _isSuggestionsLoading.value = false
            }
        }
    }

    private fun fetchWeather(city: City) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            try {
                val weather = weatherRepository.getWeather(city)
                _weatherState.value = WeatherUiState.Success(weather)
            } catch (e: Exception) {
                _weatherState.value = WeatherUiState.Error(
                    e.localizedMessage ?: "Failed to fetch weather"
                )
            }
        }
    }
}
