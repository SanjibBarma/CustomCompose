package com.example.customcompose.compose.number_validation

import android.graphics.BitmapFactory
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customcompose.R
import com.example.customcompose.helper.SharedPrefHelper
import com.example.customcompose.model.Block
import com.example.customcompose.model.Option
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.ui.theme.ProductSelected
import com.example.customcompose.viewmodel.BlockListViewModel
import java.io.File

@Composable
fun NonRefProductList(
    block: Block,
    blockListViewModel: BlockListViewModel,
    index: Int,
    position: Int,
    isActiveGroup: Boolean
) {
    val isRequired = block.required
    val existingData = blockListViewModel.getDataFromIndex(position, index)
    val context = LocalContext.current
    var selectedBrand by remember { mutableStateOf(existingData?.answer ?: "") }
    val sharedPrefHelper =  SharedPrefHelper(context)
    val question = block.question?.slug ?: ""
    val options = block.options ?: emptyList()
    val blockId = block.id ?: ""

    var selectedItem by remember {
        mutableStateOf<Option?>(options.find { it.slug == selectedBrand })
    }

    LaunchedEffect (selectedBrand){
        val surveyHistoryModel = SurveyHistoryModel(
            question = question,
            answer = selectedBrand,
            id = blockId
        )
        blockListViewModel.saveDataAtIndex(position, surveyHistoryModel)
    }

    Column {
        println("Block Id is: ${block.id}")
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

                val backgroundColor = if (selectedItem == option) ProductSelected else Color.White

                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(100.dp)
                        .width(110.dp)
                        .background(backgroundColor)
                        .clickable(enabled = isActiveGroup) {
                            selectedBrand = option.slug!!
                            selectedItem = option

                            val surveyHistoryModel = SurveyHistoryModel(
                                question = question,
                                answer = selectedBrand,
                                id = blockId
                            )
                            blockListViewModel.saveDataAtIndex(position, surveyHistoryModel)

                            if (question == "Primary Brand"){
                                sharedPrefHelper.setPrimaryBrandName(selectedBrand)
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
                            modifier = Modifier.height(60.dp).width(100.dp).background(Color.White).padding(top = 8.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_brand_image),
                            contentDescription = "Default Icon",
                            modifier = Modifier.height(60.dp).width(100.dp).background(Color.White).padding(top = 8.dp)
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
