package com.example.customcompose.views.compose.referring.video

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.customcompose.R
import com.example.customcompose.views.CustomActivity
import com.example.customcompose.helper.CommonUtils.createVideoThumbnail
import com.example.customcompose.helper.CommonUtils.getVideoPathFromCache
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun VideoBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String
) {
    val context = LocalContext.current
    var videoThumbnail by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val currentBlockId = block.id ?: ""

    val videoFileName = block.options?.get(0)!!.value.substringAfterLast("/")
    val videoFile = File(context.cacheDir, videoFileName)

    val videoPath = getVideoPathFromCache(context, videoFileName)
    var showVideoDialogScreen by remember { mutableStateOf(false) }

    var videoUri by remember { mutableStateOf<Uri?>(null) }
    val isSkippable = block.skip?.id != "-1"

    var videoName by remember { mutableStateOf(block.surveyHistoryModel.firstOrNull()?.answer ?: "") }
    val question = block.question?.alias ?: ""

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        showVideoDialogScreen = false

        val surveyHistoryModel = SurveyHistoryModel(
            question = question,
            answer = "Yes",
            id = currentBlockId
        )
        block.surveyHistoryModel = listOf(surveyHistoryModel)

        block.options[0].referTo?.id?.let { blockId ->
            block.options[0].referTo!!.group_no?.let { groupId ->
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

    LaunchedEffect(videoPath) {
        if (videoPath != null) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val bitmap = createVideoThumbnail(videoPath)
                    withContext(Dispatchers.Main) {
                        videoThumbnail = bitmap
                        isLoading = false
                    }
                } catch (e: Exception) {
                    Log.e("Thumbnail", "Error loading thumbnail", e)
                    withContext(Dispatchers.Main) {
                        isLoading = false
                    }
                }
            }
        } else {
            isLoading = false
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

            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray)
                    .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    .clickable(enabled = isActiveGroup) {
                        if (videoThumbnail != null && videoPath != null) {
                            Log.d("Box Clicked", "Showing video dialog")
                            videoUri = Uri.fromFile(File(videoPath))
                            showVideoDialogScreen = true
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (videoThumbnail != null) {
                    Image(
                        bitmap = videoThumbnail!!.asImageBitmap(),
                        contentDescription = "Video Thumbnail",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = {
                            if (videoThumbnail != null && videoPath != null) {
                                Log.d("Box Clicked", "Showing video dialog")
                                videoUri = Uri.fromFile(File(videoPath))
                                showVideoDialogScreen = true
                            }
                        },
                        modifier = Modifier.size(60.dp),
                        enabled = isActiveGroup
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_media_play),
                            contentDescription = "Play Video",
                            tint = Color.White
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(android.R.drawable.ic_menu_report_image),
                        contentDescription = "No Video Thumbnail",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            if (showVideoDialogScreen) {
                val videoUriiiii = Uri.fromFile(videoFile)
                val intent = Intent(context, CustomActivity::class.java).apply {
                    putExtra("video_uri", videoUriiiii.toString())
                    putExtra("fileView", "video_view")
                }
                launcher.launch(intent)
            }

            if (isSkippable) {
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val surveyHistoryModel = SurveyHistoryModel(
                            question = "", answer = "", id = currentBlockId
                        )
                        block.surveyHistoryModel = listOf(surveyHistoryModel)

                        block.skip?.id?.let { blockId ->
                            block.skip.group_no.let { groupId ->
                                if (destination == "mainSurvey") {
                                    blockListViewModel.addBlockToTheSurveyFlow(
                                        blockId,
                                        groupId,
                                        block.position
                                    )
                                } else {
                                    blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                                }
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth(), enabled = isActiveGroup
                ) {
                    Text("Skip")
                }
            }

        }
    }
}