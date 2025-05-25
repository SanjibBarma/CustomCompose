package com.example.customcompose.views.compose.referring.image_view

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customcompose.views.CustomActivity
import com.example.customcompose.R
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.ui.theme.ProductSelected
import com.example.customcompose.viewmodel.BlockListViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun ImageBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    val isSkippable = block.skip?.id != "-1"
    val currentBlockId = block.id ?: ""
    val options = block.options ?: emptyList()
    val question = block.question?.alias.orEmpty()

    var selectedImage by remember { mutableStateOf(block.surveyHistoryModel.firstOrNull()?.answer.orEmpty()) }
    var selectedItem by remember { mutableStateOf(options.find { it.value == selectedImage }) }
    var showImagePreview by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(options.indexOfFirst { it.value == selectedImage }.coerceAtLeast(0)) }

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        showImagePreview = false

        block.options?.get(0)?.referTo?.id?.let { blockId ->
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

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = block.question?.slug.orEmpty())
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            val current = lazyListState.firstVisibleItemIndex
                            if (current > 0) lazyListState.animateScrollToItem(current - 1)
                        }
                    },
                    enabled = options.isNotEmpty() && lazyListState.firstVisibleItemIndex > 0
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Scroll left", tint = Color.Gray)
                }

                LazyRow(state = lazyListState, modifier = Modifier.weight(1f)) {
                    itemsIndexed(options) { index, option ->
                        val imageBitmap = rememberImageBitmapFromCache(option.value, context)
                        val isSelected = option.value == selectedImage

                        Box(
                            modifier = Modifier.border(1.dp, if (isSelected) Color.Black else Color.Transparent, RoundedCornerShape(4.dp))
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(width = 100.dp, height = 130.dp)
                                    .background(if (selectedItem == option) ProductSelected else Color.White)
                                    .clickable(enabled = isActiveGroup) {
                                        selectedImage = option.value ?: ""
                                        selectedItem = option
                                        selectedIndex = index
                                        showImagePreview = true

                                        block.surveyHistoryModel = listOf(SurveyHistoryModel(question, selectedImage, currentBlockId))

                                    }
                                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp)),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    imageBitmap?.let {
                                        Image(
                                            bitmap = it,
                                            contentDescription = option.value,
                                            modifier = Modifier.size(100.dp, 130.dp)
                                        )
                                    } ?: Image(
                                        painter = painterResource(id = R.drawable.tom_jerry),
                                        contentDescription = "Default Icon",
                                        modifier = Modifier.size(100.dp, 130.dp)
                                    )

                                    if (isSelected) {
                                        Box(
                                            Modifier
                                                .matchParentSize()
                                                .background(Color.Black.copy(alpha = 0.4f))
                                                .clip(RoundedCornerShape(4.dp))
                                        )
                                    }
                                }

                                if (!option.slug.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = option.slug,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.sp,
                                        modifier = Modifier.width(100.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            val current = lazyListState.firstVisibleItemIndex
                            if (current < options.size - 1) lazyListState.animateScrollToItem(current + 1)
                        }
                    },
                    enabled = options.isNotEmpty() && lazyListState.firstVisibleItemIndex < options.size - 1
                ) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Scroll right", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isSkippable) {
                Button(
                    onClick = {
                        block.surveyHistoryModel = listOf(SurveyHistoryModel("", "", currentBlockId))
                        block.skip?.id?.let { id ->
                            block.skip.group_no.let { groupId ->
                                if (destination == "mainSurvey") {
                                    blockListViewModel.addBlockToTheSurveyFlow(id, groupId, block.position)
                                } else {
                                    blockListViewModel.addBlockToTheCheckList(id, groupId)
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

    if (showImagePreview) {

        val json = Json.encodeToString(options) // options: List<Option>
        val intent = Intent(context, CustomActivity::class.java).apply {
            putExtra("options", json)
            putExtra("selectedIndex", selectedIndex.toString())

            putExtra("fileView", "image_view")
        }
        launcher.launch(intent)
    }
}

@Composable
fun rememberImageBitmapFromCache(imagePath: String, context: Context): ImageBitmap? {
    val fileName = imagePath.substringAfterLast("/")
    val imageFile = File(context.cacheDir, fileName)
    return remember(imagePath) {
        if (imageFile.exists()) {
            BitmapFactory.decodeFile(imageFile.absolutePath)?.asImageBitmap()
        } else null
    }
}
