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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.customcompose.R
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel
import es.dmoral.toasty.Toasty
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ImageCaptureBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String
) {
    var showCamera by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }
    val isSkippable = block.skip?.id != "-1"
    val context = LocalContext.current
    val currentBlockId = block.id ?: ""

    val cacheDir = context.cacheDir
    val cachedImageFile = File(cacheDir, block.surveyHistoryModel?.firstOrNull()?.answer ?: "")

    var capturedImageUri by remember {
        mutableStateOf<Uri?>(if (cachedImageFile.exists()) cachedImageFile.toUri() else null)
    }

    val question = block.question?.slug ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = block.question!!.slug)
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .clickable (enabled = isActiveGroup){ showCamera = true }
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .height(200.dp),
                contentAlignment = Alignment.Center,
            ) {
                val bitmap = remember(capturedImageUri) {
                    capturedImageUri?.let { uri ->
                        try {
                            val inputStream = context.contentResolver.openInputStream(uri)
                            val originalBitmap = BitmapFactory.decodeStream(inputStream)
                            inputStream?.close()

                            val exif = context.contentResolver.openInputStream(uri)
                                ?.use { ExifInterface(it) }
                            val orientation = exif?.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED
                            )

                            when (orientation) {
                                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(
                                    originalBitmap,
                                    90f
                                )

                                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(
                                    originalBitmap,
                                    180f
                                )

                                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(
                                    originalBitmap,
                                    270f
                                )

                                else -> originalBitmap
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                }

                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Captured Image",
                        modifier = Modifier.fillMaxSize().clickable(enabled = isActiveGroup) { showCamera = true },
                        contentScale = ContentScale.Crop
                    )
                } ?: run {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "Open Camera",
                        modifier = Modifier.size(100.dp),
                        tint = Color.Blue
                    )
                }
            }

            if (showCamera) {
                FullScreenDialog(onDismissRequest = { showCamera = false }) {
                    val photoFile = createImageFile(context)
                    CustomCameraPreview(
                        onCaptureClick = { uri ->
                            capturedImageUri = uri
                            saveCapturedImageToCache(context, uri)
                            showCamera = false
                            showPreview = true
                        },
                        photoFile = photoFile
                    )
                }
            }

            if (showPreview && capturedImageUri != null) {
                FullScreenDialog(onDismissRequest = { }) {
                    ImagePreview(
                        imageUri = capturedImageUri!!,
                        onRetake = {
                            showPreview = false
                            capturedImageUri = null
                            showCamera = true
                        },
                        onForward = {
                            showPreview = false
                            val surveyHistoryModel = SurveyHistoryModel(
                                question = question,
                                answer = capturedImageUri!!.lastPathSegment ?: "",
                                id = currentBlockId
                            )
                            block.surveyHistoryModel = listOf(surveyHistoryModel)

                            block.referTo?.id?.let { refBlockId ->
                                block.referTo.group_no?.let { groupId ->
                                    if (destination == "mainSurvey") {
                                        blockListViewModel.addBlockToTheSurveyFlow(refBlockId, groupId)
                                    } else {
                                        blockListViewModel.addBlockToTheCheckList(refBlockId, groupId)
                                    }
                                }
                            }
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isSkippable) {
                Button(
                    onClick = {
                        val surveyHistoryModel = SurveyHistoryModel(
                            question = "",
                            answer = "",
                            id = currentBlockId
                        )
                        block.surveyHistoryModel = listOf(surveyHistoryModel)

                        block.skip?.id?.let { skipBlockId ->
                            block.skip.group_no?.let { groupId ->
                                if (destination == "mainSurvey") {
                                    blockListViewModel.addBlockToTheSurveyFlow(skipBlockId, groupId)
                                } else {
                                    blockListViewModel.addBlockToTheCheckList(skipBlockId, groupId)
                                }
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
}


@Composable
fun FullScreenDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        )
    ) {

        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            content()
        }

    }
}

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
                .padding(16.dp)
                .padding(bottom = 50.dp)
                .navigationBarsPadding() ,
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
fun saveCapturedImageToCache(context: Context, uri: Uri) {
    val inputStream = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()

    val fileName = uri.lastPathSegment ?: "captured_image.jpg"
    val file = File(context.cacheDir, fileName)

    try {
        FileOutputStream(file).use { out ->
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
fun rotateImageIfRequired(context: Context, uri: Uri, file: File) {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val exif = inputStream?.use { ExifInterface(it) }
        val orientation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            else -> bitmap
        }

        // ফিক্সড ইমেজ ফাইল হিসেবে পুনরায় সেভ করা হচ্ছে
        FileOutputStream(file).use { out ->
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
