# WeatherSnap 🌤️📸

A native Android app that lets users search live weather, capture photos with a custom CameraX interface, compress images, save annotated reports locally, and view saved reports.

## Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| DI | Hilt |
| Navigation | Navigation Compose |
| Networking | Retrofit + Gson + OkHttp |
| Database | Room |
| Camera | CameraX |
| Async | Coroutines + Flow |
| Image Loading | Coil |

## API

Uses [Open-Meteo](https://open-meteo.com/) (no API key required):
- **Geocoding**: `https://geocoding-api.open-meteo.com/v1/search`
- **Forecast**: `https://api.open-meteo.com/v1/forecast`

## Project Structure

```
app/src/main/java/com/weathersnap/
├── WeatherSnapApplication.kt      # Hilt Application
├── MainActivity.kt                 # Single Activity
├── backend/                        # Data Layer
│   ├── api/                        # Retrofit services + DTOs
│   ├── db/                         # Room database + DAO + Entity
│   ├── di/                         # Hilt modules
│   ├── model/                      # Domain models
│   ├── repository/                 # Repositories
│   └── util/                       # WeatherCodeMapper, ImageCompressor
└── frontend/                       # UI Layer
    ├── ui/
    │   ├── components/             # Reusable composables
    │   ├── navigation/             # NavGraph + Screen routes
    │   ├── screens/                # 4 app screens
    │   └── theme/                  # Material 3 theme
    └── viewmodel/                  # ViewModels
```

## Setup & Run

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34

### Steps
1. Clone or extract the project
2. Open in Android Studio
3. Let Gradle sync complete
4. Connect a device or start an emulator (API 26+)
5. Click **Run ▶️** or use `./gradlew installDebug`

### Generate Gradle Wrapper (if missing)
```bash
gradle wrapper --gradle-version 8.4
```

## App Screens

1. **Weather Screen** — City search with autocomplete + live weather
2. **Create Report** — Weather snapshot + photo capture + notes
3. **Custom Camera** — CameraX live preview + capture + compression
4. **Saved Reports** — All reports from Room DB with images and metadata

## Key Features

- ✅ City search with **debounced autocomplete** (300ms)
- ✅ **In-memory caching** of city suggestions
- ✅ Live weather from Open-Meteo (temperature, condition, humidity, wind, pressure)
- ✅ Custom **CameraX** camera (no intent)
- ✅ **Image compression** with original/compressed size tracking
- ✅ Local persistence with **Room DB** (IO-thread operations)
- ✅ Smooth **navigation transitions** (slide + fade)
- ✅ Animated UI states (loading, success, error, suggestions)
- ✅ **Debug-only** OkHttp logging interceptor
- ✅ Material 3 dark theme

## Developer Judgment Challenge Implementation

**Requirement:** Ensure in-progress reports survive process death and prevent temporary image leaks.

**Approach & Tradeoffs:**
1. **Process Death Recovery:** The `ReportViewModel` has been updated to use `SavedStateHandle` instead of standard `MutableStateFlow` for holding the in-progress `notes`, `imagePath`, and image sizes. If the user backgrounds the app and Android kills the process to free memory, the state is preserved and restored perfectly when the app is reopened.
2. **Preventing Indefinite Leaks:** `ImageCompressor` originally saved compressed images directly to `context.filesDir`. This meant if a user cancelled the report, the image would be orphaned and leak storage indefinitely. To fix this, I changed the compression output directory to `context.cacheDir`. The Android OS automatically cleans `cacheDir` when space is needed. 
3. **Persisting Saved Images:** When the user explicitly clicks "Save Report", the `ReportViewModel` copies the temporary image from `cacheDir` to `filesDir` (which is persistent) and saves the report with the new permanent path. This perfectly balances safe persistence for saved reports and automatic cleanup for abandoned ones.
