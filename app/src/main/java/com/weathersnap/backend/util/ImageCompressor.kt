package com.weathersnap.backend.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for compressing captured images.
 * Compresses JPEG images to reduce file size before saving reports.
 */
@Singleton
class ImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Result of image compression containing file path and size info.
     */
    data class CompressionResult(
        val compressedFile: File,
        val originalSize: Long,
        val compressedSize: Long
    )

    /**
     * Compresses the given image file with the specified JPEG quality.
     * @param originalFile The original captured image file.
     * @param quality JPEG compression quality (0-100). Default is 50.
     * @return CompressionResult with compressed file path and sizes.
     */
    fun compressImage(originalFile: File, quality: Int = 50): CompressionResult {
        val originalSize = originalFile.length()

        // Decode the original bitmap
        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath)

        // Create the compressed output file in app's internal storage
        val compressedFile = File(
            context.filesDir,
            "compressed_${System.currentTimeMillis()}.jpg"
        )

        // Compress and write
        FileOutputStream(compressedFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        }

        // Recycle bitmap to free memory
        bitmap.recycle()

        val compressedSize = compressedFile.length()

        return CompressionResult(
            compressedFile = compressedFile,
            originalSize = originalSize,
            compressedSize = compressedSize
        )
    }
}
