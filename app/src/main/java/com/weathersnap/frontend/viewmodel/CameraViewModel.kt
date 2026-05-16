package com.weathersnap.frontend.viewmodel

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.lifecycle.ViewModel
import com.weathersnap.backend.util.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * UI state for the camera capture flow.
 */
sealed class CameraUiState {
    /** Camera is previewing, ready to capture. */
    object Ready : CameraUiState()

    /** Image is being captured and compressed. */
    object Capturing : CameraUiState()

    /** Capture completed successfully. */
    data class Captured(
        val compressedPath: String,
        val originalSize: Long,
        val compressedSize: Long
    ) : CameraUiState()

    /** An error occurred during capture. */
    data class Error(val message: String) : CameraUiState()
}

/**
 * ViewModel for the Custom Camera screen.
 * Handles image capture using CameraX and compression.
 */
@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val _cameraState = MutableStateFlow<CameraUiState>(CameraUiState.Ready)
    val cameraState: StateFlow<CameraUiState> = _cameraState.asStateFlow()

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    /**
     * Captures an image using the provided ImageCapture use case.
     * The image is saved to a temp file, then compressed.
     */
    fun captureImage(imageCapture: ImageCapture) {
        _cameraState.value = CameraUiState.Capturing

        // Create a temporary file for the original image
        val originalFile = File(
            context.cacheDir,
            "original_${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(originalFile).build()

        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    try {
                        // Compress the captured image
                        val result = imageCompressor.compressImage(originalFile)

                        // Clean up the original file
                        originalFile.delete()

                        _cameraState.value = CameraUiState.Captured(
                            compressedPath = result.compressedFile.absolutePath,
                            originalSize = result.originalSize,
                            compressedSize = result.compressedSize
                        )
                    } catch (e: Exception) {
                        _cameraState.value = CameraUiState.Error(
                            e.localizedMessage ?: "Compression failed"
                        )
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    _cameraState.value = CameraUiState.Error(
                        exception.localizedMessage ?: "Capture failed"
                    )
                }
            }
        )
    }

    /** Resets camera state back to ready. */
    fun resetState() {
        _cameraState.value = CameraUiState.Ready
    }

    override fun onCleared() {
        super.onCleared()
        cameraExecutor.shutdown()
    }
}
