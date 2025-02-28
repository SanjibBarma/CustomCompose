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
import com.example.customcompose.model.TARGET_ACHIEVEMENT_LIST
import com.example.customcompose.model.TargetAchievement
import com.example.customcompose.ui.theme.ProductSelected
import com.example.customcompose.viewmodel.BlockListViewModel
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
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

    val gson = Gson()
    val targetAchievementList: List<TargetAchievement> = gson.fromJson(TARGET_ACHIEVEMENT_LIST, Array<TargetAchievement>::class.java).toList()

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
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = isActiveGroup) { }
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

                            val sourceLocation = blockListViewModel.routeListItem.value
                                .flatMap { it.locationList.orEmpty() } // fetching all the data from locationList
                                .flatMap { it.surveyHistoryModel.orEmpty() } // now fetching all from surveyHistoryModel which is under locationList
                                .mapNotNull { it?.id } // accepts null from surveyHistoryModel taking all the answer
                                .firstOrNull()

                            println("source_location: ${sourceLocation?.toInt()}")

                            //match the primary brand alias
                            if (block.question?.alias == "product"){
                                var matched = false
                                println("print_log 1")
                                for(achievementModel in targetAchievementList){
                                    println("print_log 2")
                                    if (achievementModel.products.size != 0){
                                        println("print_log 3")
                                        for (locationTarget in achievementModel.locations){
                                            println("print_log 4")
                                            println("sourceLocation: $sourceLocation    locationTarget.id: ${locationTarget.id}")

                                            //current block er alias jodi product hoy
                                            //and target achievement list er product list size jodi 0 na hoy
                                            //then selected route location id
                                            if (sourceLocation?.toInt() == locationTarget.id){
                                                println("print_log 5")
                                                for (productTarget in achievementModel.products){
                                                    println("print_log 6")
                                                    if (option.alias == productTarget.id){
                                                        println("print_log 7")
                                                        if (achievementModel.daily_achievement >= achievementModel.daily_target && !achievementModel.over_achivement){
                                                            println("print_log 8")
                                                            Toasty.warning(context, "No more target for this location and products combination", Toasty.LENGTH_SHORT).show()
                                                            return@clickable
                                                        }
                                                        matched = true
                                                    }
                                                }
                                            }
                                        }
                                    }else{
                                        println("print_log else 2")
                                        matched = true
                                    }
                                }
                                if (!matched){
                                    Toasty.warning(context, "This product is not allowed for this location", Toasty.LENGTH_SHORT).show()
                                    return@clickable
                                }
                            }
                            selectedBrand = option.slug!!
                            selectedItem = option

                            val surveyHistoryModel = SurveyHistoryModel(
                                question = question,
                                answer = selectedBrand,
                                id = blockId
                            )
                            blockListViewModel.saveDataAtIndex(position, surveyHistoryModel)

                            if (question == "Primary Brand") {
                                sharedPrefHelper.setPrimaryBrandName(selectedBrand)
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
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_brand_image),
                            contentDescription = "Default Icon",
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
