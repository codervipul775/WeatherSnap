package com.weathersnap.frontend.ui.navigation

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.weathersnap.backend.model.Weather
import com.weathersnap.frontend.ui.screens.*
import com.weathersnap.frontend.viewmodel.*

@Composable
fun WeatherSnapNavGraph(navController: NavHostController) {
    val gson = remember { Gson() }

    NavHost(
        navController = navController,
        startDestination = Screen.Weather.route,
        enterTransition = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) },
        exitTransition = { slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300)) },
        popEnterTransition = { slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300)) },
        popExitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300)) }
    ) {
        // ── Weather Screen ──
        composable(Screen.Weather.route) {
            val viewModel: WeatherViewModel = hiltViewModel()
            WeatherScreen(
                viewModel = viewModel,
                onCreateReport = { weather ->
                    val json = Uri.encode(gson.toJson(weather))
                    navController.navigate(Screen.CreateReport.createRoute(json))
                },
                onViewReports = {
                    navController.navigate(Screen.SavedReports.route)
                }
            )
        }

        // ── Create Report Screen ──
        composable(
            route = Screen.CreateReport.route,
            arguments = listOf(navArgument("weatherJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val weatherJson = backStackEntry.arguments?.getString("weatherJson") ?: ""
            val weather = gson.fromJson(Uri.decode(weatherJson), Weather::class.java)
            val viewModel: ReportViewModel = hiltViewModel()

            // Listen for camera result from back stack
            val savedStateHandle = backStackEntry.savedStateHandle
            val imagePath = savedStateHandle.get<String>("image_path")
            val originalSize = savedStateHandle.get<Long>("original_size") ?: 0L
            val compressedSize = savedStateHandle.get<Long>("compressed_size") ?: 0L
            if (imagePath != null) {
                viewModel.setImageData(imagePath, originalSize, compressedSize)
                savedStateHandle.remove<String>("image_path")
                savedStateHandle.remove<Long>("original_size")
                savedStateHandle.remove<Long>("compressed_size")
            }

            CreateReportScreen(
                weather = weather,
                viewModel = viewModel,
                onCapturePhoto = { navController.navigate(Screen.Camera.route) },
                onBack = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.navigate(Screen.SavedReports.route) {
                        popUpTo(Screen.Weather.route) { inclusive = false }
                    }
                }
            )
        }

        // ── Camera Screen ──
        composable(Screen.Camera.route) {
            val viewModel: CameraViewModel = hiltViewModel()
            CameraScreen(
                viewModel = viewModel,
                onImageCaptured = { path, originalSize, compressedSize ->
                    navController.previousBackStackEntry?.savedStateHandle?.apply {
                        set("image_path", path)
                        set("original_size", originalSize)
                        set("compressed_size", compressedSize)
                    }
                    navController.popBackStack()
                },
                onClose = { navController.popBackStack() }
            )
        }

        // ── Saved Reports Screen ──
        composable(Screen.SavedReports.route) {
            val viewModel: SavedReportsViewModel = hiltViewModel()
            SavedReportsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
