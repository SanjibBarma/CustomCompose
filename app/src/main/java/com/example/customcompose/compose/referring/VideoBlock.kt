package com.example.customcompose.compose.referring

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.example.customcompose.R
import com.example.customcompose.helper.CommonUtils.createVideoThumbnail
import com.example.customcompose.helper.CommonUtils.getVideoPathFromCache
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.max

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
//    val videoFileName = "X promo.mp4"
    val currentBlockId = block.id ?: ""

    val videoFileName = block.options?.get(0)!!.value.substringAfterLast("/")
    val imageFile = File(context.cacheDir, videoFileName)

    val videoPath = getVideoPathFromCache(context, videoFileName)
    var showDialog by remember { mutableStateOf(false) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    val isSkippable = block.skip?.id != "-1"

    var videoName by remember { mutableStateOf(block.surveyHistoryModel?.firstOrNull()?.answer ?: "") }
    val question = block.question?.slug ?: ""


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
                            videoUri = Uri.fromFile(File(videoPath)) // Set the video Uri
                            showDialog = true // Show the dialog
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
                                videoUri = Uri.fromFile(File(videoPath)) // Set the video Uri
                                showDialog = true // Show the dialog
                            }
                        },
                        modifier = Modifier.size(60.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_media_play),
                            contentDescription = "Play Video",
                            tint = androidx.compose.ui.graphics.Color.White
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

            if (showDialog && videoUri != null) {
                val context = LocalContext.current
                val density = LocalDensity.current
                val screenWidthPx = remember { context.resources.displayMetrics.widthPixels }
                val screenHeightPx = remember { context.resources.displayMetrics.heightPixels }

                Dialog(
                    onDismissRequest = { /* ... */ },
                    properties = DialogProperties(
                        usePlatformDefaultWidth = false,
                        decorFitsSystemWindows = false
                    ),
                ) {

                    // Get the DialogWindow
                    val dialogWindow =
                        (LocalLifecycleOwner.current as? DialogWindowProvider)?.window
                    // Set LayoutParams to match screen size
                    if (dialogWindow != null) {
                        dialogWindow.attributes?.let { attributes ->
                            attributes.width = screenWidthPx
                            attributes.height = screenHeightPx
                            dialogWindow.attributes = attributes
                        }
                    }

                    AndroidView(
                        factory = { context ->
                            val textureView = TextureView(context).apply {
                                layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }

                            val mediaPlayer = MediaPlayer()

                            textureView.surfaceTextureListener =
                                object : TextureView.SurfaceTextureListener {
                                    override fun onSurfaceTextureAvailable(
                                        surface: SurfaceTexture,
                                        width: Int,
                                        height: Int
                                    ) {
                                        mediaPlayer.apply {
                                            setDataSource(context, videoUri!!)
                                            setSurface(Surface(surface))
                                            setOnPreparedListener { mp ->
                                                // Get video dimensions
                                                val videoWidth = mp.videoWidth
                                                val videoHeight = mp.videoHeight

                                                // Calculate transformation matrix
                                                val matrix = Matrix()
                                                val viewWidth = textureView.width.toFloat()
                                                val viewHeight = textureView.height.toFloat()

                                                // Rotate 90 degrees around center
                                                matrix.postRotate(
                                                    90f,
                                                    viewWidth / 2,
                                                    viewHeight / 2
                                                )

                                                // Scale to maintain aspect ratio after rotation
                                                val scale = max(
                                                    viewHeight / videoWidth,
                                                    viewWidth / videoHeight
                                                )
                                                matrix.postScale(
                                                    scale,
                                                    scale,
                                                    viewWidth / 2,
                                                    viewHeight / 2
                                                )

                                                // Apply transformation
                                                textureView.setTransform(matrix)
                                                start()
                                            }

                                            setOnCompletionListener {
                                                showDialog = false

                                                val surveyHistoryModel = SurveyHistoryModel(
                                                    question = question,
                                                    answer = "Yes",
                                                    id = currentBlockId
                                                )
                                                block.surveyHistoryModel = listOf(surveyHistoryModel)

                                                block.options[0].referTo?.id?.let { blockId ->
                                                    block.options[0].referTo!!.group_no?.let { groupId ->
                                                        if (destination == "mainSurvey") {
                                                            blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId, block.position)
                                                        } else {
                                                            blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                                                        }

                                                    }
                                                }
                                            }

                                            prepareAsync()
                                        }
                                    }

                                    override fun onSurfaceTextureSizeChanged(
                                        surface: SurfaceTexture,
                                        width: Int,
                                        height: Int
                                    ) {
                                    }

                                    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                                        mediaPlayer.release()

                                        return true
                                    }

                                    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                                }
                            textureView
                        },
                        modifier = Modifier
                            .fillMaxSize() // This is important, but the Dialog properties are key
                    )
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
            }

        }
    }
}

