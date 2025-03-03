package com.example.customcompose.compose.referring.image_capture

import android.app.Activity
import android.hardware.camera2.CameraAccessException
import android.net.Uri
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.customcompose.R
import com.example.customcompose.common_utils.CommonUtils.rotateImageIfRequired
import es.dmoral.toasty.Toasty
import java.io.File

@Composable
fun CustomCameraPreview(onCaptureClick: (Uri) -> Unit, photoFile: File?) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context as? Activity

    val preview = remember { Preview.Builder().build() }
//    val imageCapture = remember { ImageCapture.Builder().build() }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9) // 16:9 Aspect Ratio
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }

    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    val cameraSelector = remember(lensFacing) {
        CameraSelector.Builder().requireLensFacing(lensFacing).build()
    }

    val previewView = remember { PreviewView(context) }

    LaunchedEffect(cameraSelector) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            try {
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                preview.setSurfaceProvider(previewView.surfaceProvider)
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize().navigationBarsPadding() ,
            update = { it.scaleType = PreviewView.ScaleType.FILL_CENTER }
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .size(70.dp)
                .background(Color.White, CircleShape)
                .clickable {
                    val photoURI = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        photoFile!!
                    )

                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                    imageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                val savedUri = Uri.fromFile(photoFile)

                                rotateImageIfRequired(context, savedUri, photoFile!!)

                                onCaptureClick(savedUri)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                val errorMessage = when (exception.cause) {
                                    is CameraAccessException -> "Camera access error"
                                    else -> "Image capture failed: ${exception.message}"
                                }
                                Toasty.error(context, errorMessage, Toasty.LENGTH_SHORT).show()

                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = "Capture",
                modifier = Modifier.size(50.dp),
                tint = Color.Black
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 60.dp, end = 32.dp)
                .size(32.dp)
                .background(Color.White, CircleShape)
                .clickable {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                        CameraSelector.LENS_FACING_FRONT
                    } else {
                        CameraSelector.LENS_FACING_BACK
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_sync_icon),
                contentDescription = "Switch Camera",
                modifier = Modifier.size(18.dp),
                tint = Color.Black
            )
        }
    }
}