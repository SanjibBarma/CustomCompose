package com.example.customcompose.compose.referring

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import java.io.File

@Composable
fun BrandBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String
) {
    val currentBlockId = block.id ?: ""

    val isSkippable = block.skip?.id != "-1"
    val existingData = if (destination == "mainSurvey") {
        blockListViewModel.getData(currentBlockId)
    } else {
        blockListViewModel.getDataFromCheckList(currentBlockId)
    }

    // If existing data is present, initialize selectedBrand and selectedItem with the saved answer.
    var selectedBrand by remember { mutableStateOf(existingData?.firstOrNull()?.answer ?: "") }

    val question = block.question?.slug ?: ""
    val options = block.options ?: emptyList()
    val context = LocalContext.current

    var selectedItem by remember {
        mutableStateOf<Option?>(options.find { it.slug == selectedBrand })
    }

//    LaunchedEffect (selectedBrand){
//        val surveyHistoryModel = SurveyHistoryModel(
//            question = question,
//            answer = selectedBrand,
//            id = blockId
//        )
//        blockListViewModel.saveDataAtIndex(position, surveyHistoryModel)
//    }

    Column {
        println("Block Id is: $currentBlockId")
        Text(text = question)

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth()
                .clickable (enabled = isActiveGroup){  }
        ) {
            items(options) { option ->

                val fileName = option.value.substringAfterLast("/")
                val imageFile = File(context.cacheDir, fileName) // 🔹 Use only filename

                val imageBitmap = remember(option.value) { // Cache bitmap
                    if (imageFile.exists()) {
                        BitmapFactory.decodeFile(imageFile.absolutePath)?.asImageBitmap()
                    } else null
                }

                val backgroundColor =
                    if (selectedItem == option) ProductSelected else Color.White

                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(100.dp)
                        .width(110.dp)
                        .background(backgroundColor)
                        .clickable(enabled = isActiveGroup) {
                            selectedBrand = option.slug!!
                            selectedItem = option

                            // Save the new selection
                            val surveyHistoryModel = listOf(
                                SurveyHistoryModel(
                                    question = question,
                                    answer = selectedBrand,
                                    id = currentBlockId
                                )
                            )

                            option.referTo?.group_no?.let { groupId ->
                                option.referTo.id?.let { nextBlockId ->
                                    if (destination == "mainSurvey") {
                                        blockListViewModel.saveData(currentBlockId, surveyHistoryModel)
                                        blockListViewModel.addBlockToTheSurveyFlow(nextBlockId, groupId)
                                    }else{
                                        blockListViewModel.saveDataToCheckList(currentBlockId, surveyHistoryModel)
                                        blockListViewModel.addBlockToTheCheckList(nextBlockId, groupId)
                                    }
                                }
                            }

                        }
                        .border(1.dp, color = Color.Gray, RoundedCornerShape(8.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(4.dp))

                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = option.value,
                            modifier = Modifier.height(60.dp).width(100.dp).background(Color.White)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_brand_image),
                            contentDescription = "Default Icon",
                            modifier = Modifier.height(60.dp).width(100.dp).background(Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = option.slug!!,
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

        Spacer(modifier = Modifier.height(8.dp))

        if (isSkippable) {

            Button(
                onClick = {
                    val surveyHistoryModel = listOf(
                        SurveyHistoryModel(
                            question = "",
                            answer = "",
                            id = currentBlockId
                        )
                    )
//                    blockListViewModel.saveData(currentBlockId, surveyHistoryModel)

                    block.skip?.id?.let { blockId ->
                        block.skip.group_no.let { groupId ->
//                            blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId)
                            if (destination == "mainSurvey") {
                                blockListViewModel.saveData(currentBlockId, surveyHistoryModel)
                                blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId)
                            }else{
                                blockListViewModel.saveDataToCheckList(currentBlockId, surveyHistoryModel)
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