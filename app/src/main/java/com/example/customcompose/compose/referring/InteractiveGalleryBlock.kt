package com.example.customcompose.compose.referring

import android.graphics.BitmapFactory
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customcompose.R
import com.example.customcompose.model.Block
import com.example.customcompose.model.Option
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.ui.theme.ProductSelected
import com.example.customcompose.viewmodel.BlockListViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun InteractiveGalleryBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String
) {
    val currentBlockId = block.id ?: ""

    val isSkippable = block.skip?.id != "-1"
    var selectedImage by remember { mutableStateOf(block.surveyHistoryModel?.firstOrNull()?.answer ?: "") }
    val options = block.options ?: emptyList()
    var selectedItem by remember {
        mutableStateOf<Option?>(options.find { it.value == selectedImage })
    }

    val question = block.question?.slug ?: ""
    val context = LocalContext.current

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val hasItems = options.isNotEmpty()


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
    ){
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
        println("Block Id is: $currentBlockId")
        Text(text = question)

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        val currentFirst = lazyListState.firstVisibleItemIndex
                        if (currentFirst > 0) {
                            lazyListState.animateScrollToItem(currentFirst - 1)
                        }
                    }
                },
                enabled = hasItems && lazyListState.firstVisibleItemIndex > 0
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Scroll left",
                    tint = Color.Gray
                )
            }
            LazyRow(
                state = lazyListState,
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = isActiveGroup) { }
            ) {
                items(options) { option ->

                    val fileName = option.value.substringAfterLast("/")
                    val imageFile = File(context.cacheDir, fileName)

                    val imageBitmap = remember(option.value) {
                        if (imageFile.exists()) {
                            BitmapFactory.decodeFile(imageFile.absolutePath)?.asImageBitmap()
                        } else null
                    }

                    val backgroundColor = if (selectedItem == option) ProductSelected else Color.White

                    val isSelected = option.value == selectedImage

                    Box(
                        modifier = Modifier
                            .border(1.dp, if (isSelected) Color.Blue else Color.Transparent, RoundedCornerShape(8.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .height(130.dp)
                                .width(100.dp)
                                .background(backgroundColor)
                                .clickable(enabled = isActiveGroup) {
                                    selectedImage = option.value!!
                                    selectedItem = option

                                    val surveyHistoryModel = SurveyHistoryModel(
                                        question = question,
                                        answer = selectedImage,
                                        id = currentBlockId
                                    )
                                    block.surveyHistoryModel = listOf(surveyHistoryModel)

                                    option.referTo?.group_no?.let { groupId ->
                                        option.referTo.id?.let { nextBlockId ->
                                            if (destination == "mainSurvey") {
                                                blockListViewModel.addBlockToTheSurveyFlow(nextBlockId, groupId)
                                            } else {
                                                blockListViewModel.addBlockToTheCheckList(nextBlockId, groupId)
                                            }
                                        }
                                    }

                                }
                                .border(1.dp, color = Color.Gray, RoundedCornerShape(8.dp)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(4.dp))

                            Box(contentAlignment = Alignment.Center) {
                                if (imageBitmap != null) {
                                    Image(
                                        bitmap = imageBitmap,
                                        contentDescription = option.value,
                                        modifier = Modifier
                                            .height(130.dp)
                                            .width(100.dp)
                                            .background(Color.White)
                                    )
                                }else{
                                    Icon(
                                        painter = painterResource(id = R.drawable.tom_jerry),
                                        contentDescription = "Default Icon",
                                        modifier = Modifier
                                            .height(130.dp)
                                            .width(100.dp)
                                            .background(Color.White)
                                    )
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(Color.Black.copy(alpha = 0.4f))
                                            .clip(RoundedCornerShape(8.dp))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                        )
                                    }
                                }
                            }


                            if (!option.slug.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = option.slug,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(100.dp),
                                    fontSize = 12.sp,
                                    fontWeight = Bold
                                )
                            }
                        }
                    }
                }
            }

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        val currentFirst = lazyListState.firstVisibleItemIndex
                        if (currentFirst < options.size - 1) {
                            lazyListState.animateScrollToItem(currentFirst + 1)
                        }
                    }
                },
                enabled = hasItems && lazyListState.firstVisibleItemIndex < options.size - 1
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    tint = Color.Gray,
                    contentDescription = "Scroll right"
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

                    block.skip?.id?.let { blockId ->
                        block.skip.group_no.let { groupId ->
                            if (destination == "mainSurvey") {
                                blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId)
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