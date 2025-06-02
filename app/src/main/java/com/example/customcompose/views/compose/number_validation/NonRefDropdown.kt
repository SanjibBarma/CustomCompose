package com.example.customcompose.views.compose.number_validation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.MyApplication.Companion.blockListViewModel
import com.example.customcompose.helper.CommonUtils.getTapAnalysisElapsedTime
import com.example.customcompose.model.Block
import com.example.customcompose.model.NumberCheckData
import com.example.customcompose.model.Result
import com.example.customcompose.model.SurveyHistoryModel
import com.google.gson.Gson

@Composable
fun NonRefDropdown(
    block: Block,
    index: Int,
    position: Int,
    isActiveGroup: Boolean,
    onOptionSelected: (Result) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val isRequired = block.required
    val gson = Gson()
    val existingData = blockListViewModel.getDataFromIndex(position, index)
    var selectedOption by remember { mutableStateOf(existingData?.answer ?: "") }
    val question = block.question?.alias ?: ""
    val blockId = block.id ?: ""

    LaunchedEffect (selectedOption){
        val nonRefData = appSessionManager.getMobileVerificationData()
        if (!nonRefData.isNullOrEmpty()) {
            println("NonRefTextInput: $nonRefData")
            val numberCheckData: NumberCheckData? = gson.fromJson(nonRefData, NumberCheckData::class.java)

            if (numberCheckData != null && numberCheckData.information != null){
                for (dynamicInfo in numberCheckData.information) {
                    if (dynamicInfo.key == question) {
                        selectedOption = dynamicInfo.value
                    }
                }
            }
        }

        val surveyHistoryModel = SurveyHistoryModel(
            question = question,
            answer = selectedOption,
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
        Text(block.question!!.slug)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .clickable(enabled = isActiveGroup) { isDropdownExpanded = true }
        ) {
            Column (modifier = Modifier
                .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = if(selectedOption == ""){"Select ${block.question.slug}"}else{selectedOption},
                        color = if (selectedOption == "") Color.Gray else Color.Black,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Arrow",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }
        ) {
            block.options?.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.value) },
                    onClick = {
                        selectedOption = option.value
                        val answerToSave = selectedOption.ifEmpty { "" }

                        val surveyHistoryModel = SurveyHistoryModel(
                            question = question,
                            answer = answerToSave,
                            id = blockId
                        )

                        blockListViewModel.saveDataAtIndex(position, surveyHistoryModel)

                        isDropdownExpanded = false
                    },
                    enabled = isActiveGroup
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
