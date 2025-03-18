package com.example.customcompose.views.compose.referring

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.MyApplication.Companion.surveyFlowViewModel
import com.example.customcompose.model.Block
import com.example.customcompose.model.GiveAbleAchievement
import com.example.customcompose.model.MaterialFinalModel
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.model.NumberCheckData
import com.example.customcompose.ui.theme.ProductSelected
import com.example.customcompose.viewmodel.BlockListViewModel
import com.google.gson.Gson
import java.io.File

@Composable
fun GiveAbleBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String
) {
    val isSkippable = block.skip?.id != "-1"
    val currentBlockId = block.id ?: ""
    var selectedOption by remember { mutableStateOf(block.surveyHistoryModel?.firstOrNull()?.answer ?: "") }
    val question = block.question?.slug ?: ""
    val materialList = remember { mutableStateListOf<MaterialFinalModel>() }
    val finalMaterialList = remember { mutableStateListOf<MaterialFinalModel>() }
    val context = LocalContext.current

    val gson = Gson()

    appSessionManager.getCampaignId()?.let { camId ->
        surveyFlowViewModel.fetchPtrDataById(appSessionManager.getBrId().toString(), camId)
    }

//    val storedMaterials: List<MaterialFinalModel> = gson.fromJson(MATERIAL_STRING, Array<MaterialFinalModel>::class.java).toList()
    var storedMaterials: List<MaterialFinalModel>? = null
    val ptrDataState = surveyFlowViewModel.prtData.observeAsState()

    var giveAbleAchievement: GiveAbleAchievement? = null

    LaunchedEffect(ptrDataState.value) {

        //first check the survey data from local. if local survey data or pending data is exist then calculate here

        giveAbleAchievement = gson.fromJson(ptrDataState.value?.ptrData, GiveAbleAchievement::class.java)

        if (giveAbleAchievement?.data != null) { // Check if data is not null before assigning
            storedMaterials = giveAbleAchievement!!.data

            val nonRefData = appSessionManager.getMobileVerificationData()
            if (!nonRefData.isNullOrEmpty()) {
                println("NonRefTextInput: $nonRefData")
                val numberCheckData: NumberCheckData? = gson.fromJson(nonRefData, NumberCheckData::class.java)
                if (numberCheckData != null && numberCheckData.materials != null){
                    for (material in numberCheckData.materials) {
                        if (!storedMaterials.isNullOrEmpty()){
                            for (strMaterial in storedMaterials!!) {
                                println("strMaterial: $strMaterial")

                                //material.qty!! from check number
                                //strMaterial.achievement!! from givable achievement
                                if (strMaterial.id == material.id && material.qty!! > strMaterial.achievement!!){
                                    materialList.add(material)
                                    println("materialList: ${gson.toJson(materialList)}")

                                    break
                                }
                            }
                        }
                    }
                }
            }

            for (material in materialList) {
                if (!block.options.isNullOrEmpty()) {
                    for (givable in block.options) {
                        if (material.id.toString() == givable.value) {
                            finalMaterialList.add(material)
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect (Unit){
        val extraMaterial = MaterialFinalModel(
            id = 0,
            name = "None",
            qty = 0,
            achievement = 0,
            type = 0,
            typeName = "",
            img_url = ""
        )

        finalMaterialList.add(0, extraMaterial)
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
            Text(question)
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 1000.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(finalMaterialList ?: emptyList()) { option ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .height(80.dp)
                                .width(100.dp)
                                .border(1.dp, if (selectedOption == option.id.toString()) Color.Gray else Color.LightGray, RoundedCornerShape(4.dp))
                                .clickable(enabled = isActiveGroup) {
                                    selectedOption = option.id.toString()

                                    val surveyHistoryModel = SurveyHistoryModel(
                                        question = question,
                                        answer = selectedOption!!,
                                        id = currentBlockId
                                    )
                                    block.surveyHistoryModel = listOf(surveyHistoryModel)
                                    println("Selected options Id : $selectedOption")

                                    if(selectedOption == "0"){
                                        block.referTo?.group_no?.let { groupId ->
                                            block.referTo.id?.let { nextBlockId ->
                                                if (destination == "mainSurvey"){
                                                    blockListViewModel.addBlockToTheSurveyFlow(nextBlockId, groupId, block.position)
                                                }else{
                                                    blockListViewModel.addBlockToTheCheckList(nextBlockId, groupId)
                                                }
                                            }
                                        }
                                    }else{
                                        if (!block.options.isNullOrEmpty()){
                                            for (currentPos in block.options){
                                                if (currentPos.value == selectedOption){
                                                    currentPos.referTo?.group_no?.let { groupId ->
                                                        currentPos.referTo.id?.let { nextBlockId ->
                                                            if (destination == "mainSurvey"){
                                                                blockListViewModel.addBlockToTheSurveyFlow(nextBlockId, groupId, block.position)
                                                            }else{
                                                                blockListViewModel.addBlockToTheCheckList(nextBlockId, groupId)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                .background(if (selectedOption == option.id.toString()) ProductSelected else Color.White
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (!option.img_url.isNullOrEmpty()) {
                                    val fileName = option.img_url.substringAfterLast("/")
                                    val imageFile = File(context.cacheDir, fileName)
                                    val imageBitmap = remember(option.img_url) {
                                        if (imageFile.exists()) {
                                            BitmapFactory.decodeFile(imageFile.absolutePath)?.asImageBitmap()
                                        } else null
                                    }

                                    if (imageFile.exists() && imageBitmap != null) {
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Image(
                                            bitmap = imageBitmap,
                                            contentDescription = "Material Image",
                                            modifier = Modifier.size(80.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = option.name.toString(), textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                        }
                    }
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

                        block.skip?.group_no?.let { groupId ->
                            block.skip.id.let { blockId ->
                                if (destination == "mainSurvey") {
                                    blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId, block.position)
                                } else {
                                    blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    ),
                    enabled = isActiveGroup
                ) {
                    Text("Skip")
                }
            }
        }
    }
}