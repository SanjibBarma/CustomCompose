package com.example.customcompose.views.compose.number_validation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.MyApplication.Companion.blockListViewModel
import com.example.customcompose.R
import com.example.customcompose.helper.CommonUtils
import com.example.customcompose.helper.CommonUtils.getTapAnalysisElapsedTime
import com.example.customcompose.helper.Constants.surveyBasicInfo
import com.example.customcompose.model.Block
import com.example.customcompose.model.Option
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.model.NumberCheckData
import com.example.customcompose.model.Result
import com.example.customcompose.ui.theme.ProductSelected
import com.example.customcompose.views.compose.referring.image_view.rememberImageBitmapFromCache
import com.google.gson.Gson
import java.io.File

@Composable
fun NonRefProductList(
    block: Block,
    index: Int,
    position: Int,
    isActiveGroup: Boolean,
    onOptionSelected: (Result) -> Unit
) {
    val isRequired = block.required
    val existingData = blockListViewModel.getDataFromIndex(position, index)
    val context = LocalContext.current
    var selectedBrand by remember { mutableStateOf(existingData?.answer ?: "") }
    val question = block.question?.alias ?: ""
    val options = block.options ?: emptyList()
    val blockId = block.id ?: ""
    val gson = Gson()


    var selectedItem by remember {
        mutableStateOf<Option?>(options.find { it.alias.toString() == selectedBrand })
    }

    LaunchedEffect(selectedBrand) {
        val nonRefData = appSessionManager.getMobileVerificationData()
        if (!nonRefData.isNullOrEmpty()) {
            println("NonRefTextInput: $nonRefData")
            val numberCheckData: NumberCheckData? =
                gson.fromJson(nonRefData, NumberCheckData::class.java)

            if (numberCheckData != null && numberCheckData.information != null) {
                for (dynamicInfo in numberCheckData.information) {
                    if (dynamicInfo.key == question) {
                        selectedBrand = dynamicInfo.value
                    }
                }
            }
        }

        val surveyHistoryModel = SurveyHistoryModel(
            question = question,
            answer = selectedBrand,
            id = blockId
        )
        blockListViewModel.saveDataAtIndex(position, surveyHistoryModel)

        val result = Result(
            option = question,
            tap_time = (getTapAnalysisElapsedTime()!! / 1000000).toString()
        )
        onOptionSelected(result)
    }

    Column {
        println("Block Id is: ${block.id}")
        Text(text = block.question!!.slug)

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = isActiveGroup) { }
        ) {
            items(options) { option ->
                val fileName = option.value.substringAfterLast("/")
                val imageFile = File(context.cacheDir, fileName)
                val imageBitmap = rememberImageBitmapFromCache(option.value, context, R.drawable.ic_brand_image)

                val backgroundColor = if (selectedItem == option) ProductSelected else Color.White

                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(100.dp)
                        .width(110.dp)
                        .background(backgroundColor)
                        .clickable(enabled = isActiveGroup) {
                            val sourceLocation = blockListViewModel.routeParentList.value[0].selectedId
                            println("source_location: $sourceLocation")
                            if (block.question.alias == "product") {
                                surveyBasicInfo["productId"] =  option.alias.toString()
                            }

                            selectedBrand = option.alias.toString()!!
                            selectedItem = option

                            val surveyHistoryModel = SurveyHistoryModel(
                                question = question,
                                answer = selectedBrand,
                                id = blockId
                            )
                            blockListViewModel.saveDataAtIndex(position, surveyHistoryModel)

                            if (question == "Primary Brand") {
                                appSessionManager.setPrimaryBrandName(option.slug!!)
                            }
                        }
                        .border(1.dp, color = Color.Gray, RoundedCornerShape(4.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(4.dp))

                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = option.value,
                            modifier = Modifier
                                .height(60.dp)
                                .width(100.dp)
                                .background(Color.White)
                                .padding(top = 8.dp)
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
    }
}
