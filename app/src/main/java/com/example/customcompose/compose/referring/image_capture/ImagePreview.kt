package com.example.customcompose.compose.referring.image_capture

import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.customcompose.common_utils.CommonUtils.rotateImage
import java.io.File
import java.io.IOException

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