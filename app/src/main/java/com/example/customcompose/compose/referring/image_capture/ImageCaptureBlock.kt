package com.example.customcompose.compose.referring.image_capture

import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.core.net.toUri
import com.example.customcompose.R
import com.example.customcompose.helper.CommonUtils.createImageFile
import com.example.customcompose.helper.CommonUtils.rotateImage
import com.example.customcompose.helper.CommonUtils.saveCapturedImageToCache
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel
import java.io.File

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
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(4.dp),
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
                    .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    .clickable (enabled = isActiveGroup){ showCamera = true }
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
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
                                        blockListViewModel.addBlockToTheSurveyFlow(refBlockId, groupId, block.position)
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
                                    blockListViewModel.addBlockToTheSurveyFlow(skipBlockId, groupId, block.position)
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
