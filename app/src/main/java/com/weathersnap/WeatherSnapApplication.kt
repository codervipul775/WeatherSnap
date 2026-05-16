package com.weathersnap

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for WeatherSnap.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class WeatherSnapApplication : Application()
