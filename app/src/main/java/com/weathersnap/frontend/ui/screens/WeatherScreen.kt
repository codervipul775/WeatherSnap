package com.weathersnap.frontend.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.weathersnap.backend.model.Weather
import com.weathersnap.frontend.ui.components.GradientTopBar
import com.weathersnap.frontend.ui.components.WeatherDetailCard
import com.weathersnap.frontend.ui.theme.OnPrimary
import com.weathersnap.frontend.ui.theme.Primary
import com.weathersnap.frontend.ui.theme.Surface
import com.weathersnap.frontend.ui.theme.SurfaceVariant
import com.weathersnap.frontend.ui.theme.TextSecondary
import com.weathersnap.frontend.viewmodel.WeatherUiState
import com.weathersnap.frontend.viewmodel.WeatherViewModel

/**
 * Weather Screen — allows users to search weather by city.
 * Shows city suggestions, weather data, and actions.
 */
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    onCreateReport: (Weather) -> Unit,
    onViewReports: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val citySuggestions by viewModel.citySuggestions.collectAsState()
    val isSuggestionsLoading by viewModel.isSuggestionsLoading.collectAsState()
    val weatherState by viewModel.weatherState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        // ── Top Bar ──
        GradientTopBar(
            title = "WeatherSnap",
            subtitle = "Live weather reports with camera evidence",
            action = {
                Button(
                    onClick = onViewReports,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.weathersnap.frontend.ui.theme.HeaderButtonBackground,
                        contentColor = Primary
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Reports")
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // ── Search Section ──
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Surface)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = viewModel::onSearchQueryChanged,
                            label = { Text("City") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = SurfaceVariant,
                                focusedLabelColor = Primary,
                                cursorColor = Primary,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Button(
                            onClick = viewModel::onSearchClicked,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary,
                                contentColor = OnPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Search")
                        }
                    }

                    Text(
                        text = "Enter more than 2 letters to start city suggestions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // ── City Suggestions ──
            item {
                AnimatedVisibility(
                    visible = citySuggestions.isNotEmpty(),
                    enter = slideInVertically(tween(300)) + fadeIn(tween(300)),
                    exit = slideOutVertically(tween(200)) + fadeOut(tween(200))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Surface)
                            .animateContentSize()
                    ) {
                        citySuggestions.forEach { city ->
                            Text(
                                text = city.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onCitySelected(city) }
                                    .padding(horizontal = 16.dp, vertical = 14.dp)
                            )
                        }
                    }
                }
            }

            // ── Loading Indicator for Suggestions ──
            item {
                AnimatedVisibility(visible = isSuggestionsLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Primary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            // ── Weather State ──
            item {
                when (val state = weatherState) {
                    is WeatherUiState.Idle -> {
                        // Show nothing
                    }

                    is WeatherUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Primary)
                        }
                    }

                    is WeatherUiState.Success -> {
                        Column(
                            modifier = Modifier.animateContentSize(tween(400))
                        ) {
                            WeatherDetailCard(weather = state.weather)

                            Spacer(modifier = Modifier.height(12.dp))

                            // Report readiness row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(com.weathersnap.frontend.ui.theme.SurfaceHigh)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Report readiness",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = "Camera and Room DB enabled",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Create Report Button
                            Button(
                                onClick = { onCreateReport(state.weather) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Primary,
                                    contentColor = OnPrimary
                                ),
                                shape = RoundedCornerShape(26.dp)
                            ) {
                                Text(
                                    text = "Create Report",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }

                    is WeatherUiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Surface)
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "⚠️ Error",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
