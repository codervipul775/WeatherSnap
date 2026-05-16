package com.weathersnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.weathersnap.frontend.ui.navigation.WeatherSnapNavGraph
import com.weathersnap.frontend.ui.theme.WeatherSnapTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity entry point for the WeatherSnap app.
 * Uses Jetpack Compose for all UI rendering.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherSnapTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    WeatherSnapNavGraph(
                        navController = navController
                    )
                }
            }
        }
    }
}
