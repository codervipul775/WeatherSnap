package com.weathersnap.frontend.ui.navigation

/**
 * Sealed class defining all navigation routes in the app.
 */
sealed class Screen(val route: String) {
    /** Main weather search screen. */
    object Weather : Screen("weather")

    /**
     * Create report screen — receives weather data as a JSON argument.
     * Route pattern: create_report/{weatherJson}
     */
    object CreateReport : Screen("create_report/{weatherJson}") {
        fun createRoute(weatherJson: String): String = "create_report/$weatherJson"
    }

    /** Custom CameraX camera screen. */
    object Camera : Screen("camera")

    /** Saved reports screen showing all Room DB entries. */
    object SavedReports : Screen("saved_reports")
}
