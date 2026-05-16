package com.weathersnap.frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weathersnap.backend.model.Weather
import com.weathersnap.frontend.ui.theme.HumidityGreen
import com.weathersnap.frontend.ui.theme.PressureYellow
import com.weathersnap.frontend.ui.theme.Primary
import com.weathersnap.frontend.ui.theme.SurfaceHigh
import com.weathersnap.frontend.ui.theme.SurfaceVariant
import com.weathersnap.frontend.ui.theme.TextSecondary
import com.weathersnap.frontend.ui.theme.WindBlue

/**
 * Reusable weather detail card showing city, condition, temperature, and metrics.
 * Used by Weather, CreateReport, and SavedReports screens.
 */
@Composable
fun WeatherDetailCard(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariant)
            .padding(20.dp)
    ) {
        // City name + temperature row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${weather.cityName}, ${weather.country}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = weather.condition,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }

            // Temperature badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Primary.copy(alpha = 0.2f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "${weather.temperature.toInt()}°C",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Primary,
                    fontSize = 22.sp
                )
            }
        }

        // Humidity, Wind, Pressure chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricChip(
                label = "Humidity",
                value = "${weather.humidity}%",
                valueColor = HumidityGreen,
                modifier = Modifier.weight(1f)
            )
            MetricChip(
                label = "Wind",
                value = "${weather.windSpeed} m/s",
                valueColor = WindBlue,
                modifier = Modifier.weight(1f)
            )
            MetricChip(
                label = "Pressure",
                value = "${weather.pressure.toInt()}",
                valueColor = PressureYellow,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * A single metric chip displaying label and colored value.
 */
@Composable
private fun MetricChip(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceHigh)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = valueColor
        )
    }
}
