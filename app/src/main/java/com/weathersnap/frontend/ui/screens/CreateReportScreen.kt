package com.weathersnap.frontend.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.weathersnap.backend.model.Weather
import com.weathersnap.frontend.ui.components.GradientTopBar
import com.weathersnap.frontend.ui.components.ImageSizeChips
import com.weathersnap.frontend.ui.components.WeatherDetailCard
import com.weathersnap.frontend.ui.theme.OnPrimary
import com.weathersnap.frontend.ui.theme.Primary
import com.weathersnap.frontend.ui.theme.Surface
import com.weathersnap.frontend.ui.theme.SurfaceVariant
import com.weathersnap.frontend.ui.theme.TextSecondary
import com.weathersnap.frontend.viewmodel.ReportViewModel
import java.io.File

/**
 * Create Report Screen — shows weather snapshot, image preview, notes, and save.
 */
@Composable
fun CreateReportScreen(
    weather: Weather,
    viewModel: ReportViewModel,
    onCapturePhoto: () -> Unit,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val imagePath by viewModel.imagePath.collectAsState()
    val originalSize by viewModel.originalImageSize.collectAsState()
    val compressedSize by viewModel.compressedImageSize.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    // Navigate on save success
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            onSaveSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        // ── Top Bar ──
        GradientTopBar(
            title = "Create Report",
            subtitle = "Capture, compress, annotate",
            action = {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.weathersnap.frontend.ui.theme.HeaderButtonBackground,
                        contentColor = Primary
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Back")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Weather Summary ──
            WeatherDetailCard(weather = weather)

            // ── Image Preview Section ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .padding(16.dp)
            ) {
                // Image preview area
                AnimatedContent(
                    targetState = imagePath,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "imagePreview"
                ) { path ->
                    if (path != null) {
                        Image(
                            painter = rememberAsyncImagePainter(File(path)),
                            contentDescription = "Captured photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(4f / 3f)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(4f / 3f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Photo preview",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Image size chips (visible after capture)
                if (imagePath != null) {
                    ImageSizeChips(
                        originalSize = originalSize,
                        compressedSize = compressedSize
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Capture button
                Button(
                    onClick = onCapturePhoto,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = OnPrimary
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "Capture Photo",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // ── Field Notes Section ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Field Notes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = viewModel::onNotesChanged,
                    placeholder = { Text("Notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = SurfaceVariant,
                        cursorColor = Primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            // ── Save Button ──
            Button(
                onClick = { viewModel.saveReport(weather) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = OnPrimary
                ),
                shape = RoundedCornerShape(26.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = OnPrimary,
                        modifier = Modifier.padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                }
                Text(
                    text = "Save Report",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
