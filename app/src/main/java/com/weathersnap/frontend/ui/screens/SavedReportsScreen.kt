package com.weathersnap.frontend.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.weathersnap.backend.model.Report
import com.weathersnap.frontend.ui.components.GradientTopBar
import com.weathersnap.frontend.ui.components.ImageSizeChips
import com.weathersnap.frontend.ui.theme.*
import com.weathersnap.frontend.viewmodel.SavedReportsViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedReportsScreen(
    viewModel: SavedReportsViewModel,
    onBack: () -> Unit
) {
    val reports by viewModel.reports.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).systemBarsPadding()
    ) {
        GradientTopBar(
            title = "Saved Reports",
            subtitle = "${reports.size} report${if (reports.size != 1) "s" else ""} stored locally",
            action = {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderButtonBackground, contentColor = Primary),
                    shape = RoundedCornerShape(20.dp)
                ) { Text("Back") }
            }
        )

        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            }
            reports.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📋", style = MaterialTheme.typography.displayLarge)
                        Spacer(Modifier.height(16.dp))
                        Text("No reports yet", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                        Text("Create your first weather report", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(reports, key = { it.id }) { report ->
                        ReportCard(report = report)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportCard(report: Report) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariant)
            .animateContentSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Captured image
        Image(
            painter = rememberAsyncImagePainter(File(report.imagePath)),
            contentDescription = "Report photo",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        // City + Temp row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text("${report.cityName}, ${report.country}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Text(report.condition, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Text(formatTimestamp(report.timestamp), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Box(
                Modifier.clip(RoundedCornerShape(12.dp)).background(Primary.copy(alpha = 0.2f)).padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("${report.temperature.toInt()}°C", style = MaterialTheme.typography.titleMedium, color = Primary)
            }
        }

        // Image sizes
        ImageSizeChips(originalSize = report.originalImageSize, compressedSize = report.compressedImageSize)

        // Notes
        if (report.notes.isNotBlank()) {
            Box(
                Modifier.clip(RoundedCornerShape(8.dp)).background(SurfaceHigh).padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(report.notes, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
