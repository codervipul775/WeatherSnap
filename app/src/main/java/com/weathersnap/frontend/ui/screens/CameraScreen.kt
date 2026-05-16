package com.weathersnap.frontend.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.weathersnap.frontend.ui.theme.OnPrimary
import com.weathersnap.frontend.ui.theme.Primary
import com.weathersnap.frontend.viewmodel.CameraUiState
import com.weathersnap.frontend.viewmodel.CameraViewModel

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    onImageCaptured: (path: String, originalSize: Long, compressedSize: Long) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraState by viewModel.cameraState.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    LaunchedEffect(cameraState) {
        if (cameraState is CameraUiState.Captured) {
            val c = cameraState as CameraUiState.Captured
            onImageCaptured(c.compressedPath, c.originalSize, c.compressedSize)
        }
    }

    val imageCapture = remember { ImageCapture.Builder().build() }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black).systemBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Custom Camera", style = MaterialTheme.typography.headlineLarge, color = Color.White)
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f), contentColor = Color.White),
                shape = RoundedCornerShape(20.dp)
            ) { Text("Close") }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (hasCameraPermission) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val future = ProcessCameraProvider.getInstance(ctx)
                        future.addListener({
                            val provider = future.get()
                            val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                            try {
                                provider.unbindAll()
                                provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
                            } catch (e: Exception) { Log.e("CameraScreen", "Bind failed", e) }
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Camera permission required", color = Color.White, style = MaterialTheme.typography.bodyLarge)
            }
            if (cameraState is CameraUiState.Capturing) {
                Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            }
        }

        if (cameraState is CameraUiState.Error) {
            Text((cameraState as CameraUiState.Error).message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
        }

        Button(
            onClick = { viewModel.captureImage(imageCapture) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp).height(56.dp),
            enabled = cameraState is CameraUiState.Ready,
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary),
            shape = RoundedCornerShape(28.dp)
        ) { Text("Capture", style = MaterialTheme.typography.titleMedium) }
    }
}
