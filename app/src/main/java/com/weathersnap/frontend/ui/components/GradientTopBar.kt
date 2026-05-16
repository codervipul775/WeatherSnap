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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.weathersnap.frontend.ui.theme.GradientEnd
import com.weathersnap.frontend.ui.theme.GradientStart
import com.weathersnap.frontend.ui.theme.OnBackground
import com.weathersnap.frontend.ui.theme.TextSecondary

/**
 * Reusable gradient top bar matching the UI design.
 * Shows a title, subtitle, and an optional trailing action.
 */
@Composable
fun GradientTopBar(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = com.weathersnap.frontend.ui.theme.GradientTextTitle
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = com.weathersnap.frontend.ui.theme.GradientTextSubtitle
                )
            }
            action?.invoke()
        }
    }
}
