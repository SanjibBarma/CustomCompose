package com.example.customcompose.compose.referring

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.camera2.CameraAccessException
import android.media.ExifInterface
import android.net.Uri
import android.view.WindowManager
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.customcompose.R
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel
import es.dmoral.toasty.Toasty
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ImageCaptureBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    position: Int,
    isActiveGroup: Boolean
) {
    var showCamera by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showPreview by remember { mutableStateOf(false) }
    val isSkippable = block.skip?.id != "-1"
    val context = LocalContext.current
    val photoFile = createImageFile(context)


    val existingData = blockListViewModel.getData(position)
    val question = block.question?.slug ?: ""
    val blockId = block.id ?: ""

    Column {
        Text(block.question!!.slug)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .clickable {
                    showCamera = true
                }
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {

            if (capturedImageUri != null) {
                val context = LocalContext.current
                val bitmap = remember(capturedImageUri) {
                    try {
                        val inputStream = context.contentResolver.openInputStream(capturedImageUri!!)
                        val originalBitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()

                        val exif = context.contentResolver.openInputStream(capturedImageUri!!)?.use {
                            ExifInterface(it)
                        }

                        val orientation = exif?.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED
                        )

                        // Apply rotation based on EXIF orientation
                        when (orientation) {
                            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(originalBitmap, 90f)
                            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(originalBitmap, 180f)
                            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(originalBitmap, 270f)
                            else -> originalBitmap
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }

                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Captured Image",
                        modifier = Modifier.fillMaxSize()
                            .clickable {
                                showCamera = true
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                // Show the camera icon
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "Open Camera",
                    modifier = Modifier.size(100.dp),
                    tint = Color.Blue
                )
            }

        }

        if (showCamera) {
            FullScreenDialog(onDismissRequest = { showCamera = false }) { // Use FullScreenDialog
                CustomCameraPreview(
                    onCaptureClick = { uri ->
                        capturedImageUri = uri
                        showCamera = false
                        showPreview = true
                    },
                    photoFile
                )
            }
        }

        if (showPreview && capturedImageUri != null) {
            FullScreenDialog(onDismissRequest = { /*showPreview = false*/ }) { // Use FullScreenDialog
                ImagePreview(
                    imageUri = capturedImageUri!!,
                    onRetake = {
                        showPreview = false
                        capturedImageUri = null
                        showCamera = true
                    },
                    onForward = {
                        showPreview = false

                        val surveyHistoryModel = listOf(
                            SurveyHistoryModel(
                                question = question,
                                answer = photoFile!!.name,
                                id = blockId
                            )
                        )
                        blockListViewModel.saveData(position, surveyHistoryModel)

                        block.referTo?.id?.let { blockId ->
                            block.referTo.group_no?.let { groupId ->
                                blockListViewModel.addBlockToTheList(blockId, groupId)
                            }
                        }
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isSkippable){
            Button(
                onClick = {

                    val surveyHistoryModel = listOf(
                        SurveyHistoryModel(
                            question = "",
                            answer = "",
                            id = blockId
                        )
                    )
                    blockListViewModel.saveData(position, surveyHistoryModel)

                    block.skip?.id?.let {blockId ->
                        block.skip.group_no.let { groupId ->
                            blockListViewModel.addBlockToTheList(blockId, groupId)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Skip")
            }
        }
    }
}

@Composable
fun FullScreenDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            // Add these for immersive mode if needed
            // decorFitsSystemWindows = false
        )
    ) {
        // Set flags to occupy the entire screen, including behind system bars
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Use a Layout to handle insets correctly
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black) // Or your desired background
                .windowInsetsPadding(
                    WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Vertical)
                )
        ) {
            content()
        }

        // IMPORTANT: Clear flags when the dialog is dismissed
        DisposableEffect(Unit) {
            onDispose {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            }
        }
    }
}

@Composable
fun CustomCameraPreview(
    onCaptureClick: (Uri) -> Unit,
    photoFile: File?
) {
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
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                preview.setSurfaceProvider(previewView.surfaceProvider)
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize(),
            update = { it.scaleType = PreviewView.ScaleType.FILL_CENTER }
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
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
                                onCaptureClick(photoURI)
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
                modifier = Modifier.size(48.dp),
                tint = Color.Black
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
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

@Composable
fun ImagePreview(imageUri: Uri, onRetake: () -> Unit, onForward: () -> Unit) {
    val context = LocalContext.current
    val bitmap = remember {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val exif = context.contentResolver.openInputStream(imageUri)?.use {
                ExifInterface(it)
            }

            val orientation = exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            val rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(originalBitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(originalBitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(originalBitmap, 270f)
                else -> originalBitmap
            }

            rotatedBitmap
        } catch (e: IOException) {
            null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Captured Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                text = "Error loading image",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = {
                val file = File(imageUri.path!!) // Captured image file
                if (file.exists()) {
                    file.delete() // Delete the file
                }
                onRetake() // Retake action
            }) {
                Text("Retake")
            }

            Button(onClick = {
                onForward()
            }) {
                Text("Forward")
            }
        }
    }
}

fun rotateImage(bitmap: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private fun createImageFile(context: Context): File? {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "IMG_$timeStamp.jpg"
    val storageDir: File? = context.cacheDir
    return File(storageDir, fileName)
}