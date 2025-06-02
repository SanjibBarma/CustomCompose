package com.example.customcompose.views.compose.referring

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.customcompose.MyApplication.Companion.blockListViewModel
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import java.io.InputStream

@Composable
fun FileUploadBlock(
    block: Block,
    position: Int?,
    isActiveGroup: Boolean,
    destination: String
) {
    val currentBlockId = block.id ?: ""
    val isSkippable = block.skip?.id != "-1"

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isImage by remember { mutableStateOf(false) }
    val question = block.question?.alias ?: ""
    val context = LocalContext.current
    var previousAns by remember { mutableStateOf(block.surveyHistoryModel.firstOrNull()?.answer ?: "") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it

            val mimeType = context.contentResolver.getType(it)
            isImage = mimeType?.startsWith("image") == true

            val cursor = context.contentResolver.query(it, null, null, null, null)
            cursor?.use { c ->
                val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && c.moveToFirst()) {
                    selectedFileName = c.getString(nameIndex)
                } else {
                    selectedFileName = it.lastPathSegment
                }
            }

            if (isImage) {
                val bitmap = try {
                    if (Build.VERSION.SDK_INT < 28) {
                        val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                        BitmapFactory.decodeStream(inputStream)
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    }
                } catch (e: Exception) {
                    null
                }
                selectedBitmap = bitmap
            } else {
                selectedBitmap = null
            }
        }
    }

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
            Text(block.question!!.slug)
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                selectedFileUri?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isImage && selectedBitmap != null) {
                        Image(
                            bitmap = selectedBitmap!!.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )

                        val surveyHistoryModel = SurveyHistoryModel(
                            question = question,
                            answer = "Yes",
                            id = currentBlockId
                        )
                        block.surveyHistoryModel = listOf(surveyHistoryModel)

                        block.referTo?.id?.let { blockId ->
                            block.referTo.group_no?.let { groupId ->
                                if (destination == "mainSurvey") {
                                    blockListViewModel.addBlockToTheSurveyFlow(
                                        blockId, groupId, block.position
                                    )
                                } else {
                                    blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                                }
                            }
                        }
                    } else {
                        selectedFileName?.let { name ->
                            Text("Selected File: $name")

                            val surveyHistoryModel = SurveyHistoryModel(
                                question = question,
                                answer = "Yes",
                                id = currentBlockId
                            )
                            block.surveyHistoryModel = listOf(surveyHistoryModel)

                            block.referTo?.id?.let { blockId ->
                                block.referTo.group_no?.let { groupId ->
                                    if (destination == "mainSurvey") {
                                        blockListViewModel.addBlockToTheSurveyFlow(
                                            blockId, groupId, block.position
                                        )
                                    } else {
                                        blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { launcher.launch("*/*") },
                    modifier = Modifier
                        .align(Alignment.Start)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach file",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = "Upload File")
                }
            }

            if (isSkippable) {
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val surveyHistoryModel = SurveyHistoryModel(
                            question = "",
                            answer = "",
                            id = currentBlockId
                        )
                        block.surveyHistoryModel = listOf(surveyHistoryModel)

                        block.skip?.id?.let { blockId ->
                            block.skip.group_no.let { groupId ->
                                if (destination == "mainSurvey") {
                                    blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId, block.position)
                                } else {
                                    blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isActiveGroup
                ) {
                    Text("Skip")
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
