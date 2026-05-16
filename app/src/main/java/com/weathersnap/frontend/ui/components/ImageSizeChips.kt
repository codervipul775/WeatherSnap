package com.weathersnap.frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.weathersnap.frontend.ui.theme.CompressedSizeBackground
import com.weathersnap.frontend.ui.theme.CompressedSizeText
import com.weathersnap.frontend.ui.theme.OriginalSizeBackground
import com.weathersnap.frontend.ui.theme.OriginalSizeText
import com.weathersnap.frontend.ui.theme.TextSecondary

/**
 * Displays original and compressed image size chips side by side.
 */
@Composable
fun ImageSizeChips(
    originalSize: Long,
    compressedSize: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SizeChip(
            label = "Original",
            size = originalSize,
            backgroundColor = OriginalSizeBackground,
            textColor = OriginalSizeText,
            modifier = Modifier.weight(1f)
        )
        SizeChip(
            label = "Compressed",
            size = compressedSize,
            backgroundColor = CompressedSizeBackground,
            textColor = CompressedSizeText,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SizeChip(
    label: String,
    size: Long,
    backgroundColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
        Text(
            text = formatFileSize(size),
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
    }
}

/** Formats bytes into a human-readable size string (KB / MB). */
private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
    }
}
