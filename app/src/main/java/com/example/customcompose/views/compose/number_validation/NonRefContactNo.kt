package com.example.customcompose.views.compose.number_validation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.MyApplication.Companion.blockListViewModel
import com.example.customcompose.helper.CommonUtils
import com.example.customcompose.helper.CommonUtils.getTapAnalysisElapsedTime
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.model.NumberCheckData
import com.example.customcompose.model.Result
import com.example.customcompose.viewmodel.BlockListViewModel
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NonRefContactNo(
    block: Block,
    index: Int,
    position: Int,
    isActiveGroup: Boolean,
    onOptionSelected: (Result) -> Unit
) {
    val isRequired = block.required
    val question = block.question?.alias ?: ""
    val blockId = block.id ?: ""
    val gson = Gson()
    val existingData = blockListViewModel.getDataFromIndex(position, index)
    var phoneNumber by remember { mutableStateOf(existingData?.answer ?: "") }

    // 1. Load from stored JSON data only once
    LaunchedEffect(Unit) {
        val nonRefData = appSessionManager.getMobileVerificationData()
        if (!nonRefData.isNullOrEmpty()) {
            val numberCheckData: NumberCheckData? = gson.fromJson(nonRefData, NumberCheckData::class.java)

            numberCheckData?.information?.firstOrNull { it.key == question }?.let {
                phoneNumber = it.value

                val surveyHistoryModel = SurveyHistoryModel(
                    question = question,
                    answer = it.value,
                    id = blockId
                )
                blockListViewModel.saveDataAtIndex(position, surveyHistoryModel)
            }
        }

        val result = Result(
            option = question,
            tap_time = (getTapAnalysisElapsedTime()!! / 1000000).toString()
        )
        onOptionSelected(result)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = block.question?.slug ?: "")

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
        ) {
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    if (it.all { char -> char.isDigit() } && it.length <= 10) {
                        phoneNumber = it

                        val surveyHistoryModel = SurveyHistoryModel(
                            question = question,
                            answer = it,
                            id = blockId
                        )
                        blockListViewModel.saveDataAtIndex(position, surveyHistoryModel)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                singleLine = true,
                leadingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "+880",
                            modifier = Modifier.padding(start = 8.dp, end = 4.dp),
                            color = if (isActiveGroup) Color.Black else Color.Black
                        )

                        Divider(
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                        )
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = if (isActiveGroup) Color.White else Color.LightGray,
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    disabledTextColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                enabled = isActiveGroup
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
