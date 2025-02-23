package com.example.customcompose.compose.referring

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.customcompose.helper.SharedPrefHelper
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel
import com.google.gson.Gson
import es.dmoral.toasty.Toasty

@Composable
fun CheckboxBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String
) {
    val currentBlockId = block.id ?: ""
    val isSkippable = block.skip?.id != "-1"

    val context = LocalContext.current
    val sharedPrefHelper = remember { SharedPrefHelper(context) }

    val selectedOptions = remember { mutableStateOf(block.surveyHistoryModel?.firstOrNull()?.answer?.split(",")?.toSet() ?: emptySet()) }
    val answer = selectedOptions.value.joinToString(",")
    val question = block.question?.slug ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
    ){
        Column (
            modifier = Modifier.padding(8.dp)
        ){
            Text(text = question)

            Spacer(modifier = Modifier.height(8.dp))

            block.options?.forEach { option ->
                val isSelected = selectedOptions.value.contains(option.value)

                Surface(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(if (isActiveGroup) Color.White else Color.LightGray)
                            .clickable {
                                selectedOptions.value = if (isSelected) {
                                    selectedOptions.value - option.value
                                } else {
                                    selectedOptions.value + option.value
                                }
                            }
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { isChecked ->
                                selectedOptions.value = if (isChecked) {
                                    selectedOptions.value + option.value
                                } else {
                                    selectedOptions.value - option.value
                                }

//                                if (isChecked) {
//                                    sharedPrefHelper.saveItem(option.value)
//                                }else{
//                                    sharedPrefHelper.removeItem(option.value)
//                                }
                            },
                            enabled = isActiveGroup
                        )
                        Text(option.value)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                if (isSkippable){
                    Button(
                        onClick = {

                            val surveyHistoryModel = SurveyHistoryModel(
                                question = "",
                                answer = "",
                                id = currentBlockId
                            )

                            block.skip?.group_no?.let { groupId ->
                                block.skip.id.let { blockId ->
                                    if (destination == "mainSurvey") {
                                        block.surveyHistoryModel = listOf(surveyHistoryModel)
                                        blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId)
                                    }else{
                                        blockListViewModel.saveHistoryForChecklist(currentBlockId, surveyHistoryModel)
                                        blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue,
                            contentColor = Color.White
                        ),
                        enabled = isActiveGroup
                    ) {
                        Text("Skip")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = {
                        val surveyHistoryModel = SurveyHistoryModel(
                            question = question,
                            answer = answer,
                            id = currentBlockId
                        )
                        if (answer.isEmpty()){
                            Toasty.warning(context, "Must select any one.", Toasty.LENGTH_SHORT).show()
                        }else{
//                            val gson = Gson()
//                            val comboJson = gson.toJson(selectedOptions)
//                            val setJson = gson.toJson(sharedPrefHelper.getSet())
//                            Log.d("CheckboxBlockItemSet: ", comboJson)
//                            Log.d("CheckboxBlockItemShared: ", setJson)
                            block.referTo?.group_no?.let { groupId ->
                                block.referTo.id?.let { nextBlockId ->
                                    if (destination == "mainSurvey") {
                                        block.surveyHistoryModel = listOf(surveyHistoryModel)
                                        blockListViewModel.addBlockToTheSurveyFlow(nextBlockId, groupId)
                                    }else{
                                        blockListViewModel.saveHistoryForChecklist(currentBlockId, surveyHistoryModel)
                                        blockListViewModel.addBlockToTheCheckList(nextBlockId, groupId)
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    ),
                    enabled = isActiveGroup
                ) {
                    Text("Next")
                }
            }
        }
    }
}
