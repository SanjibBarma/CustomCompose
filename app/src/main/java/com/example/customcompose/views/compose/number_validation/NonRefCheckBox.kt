package com.example.customcompose.views.compose.number_validation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.model.NumberCheckData
import com.example.customcompose.viewmodel.BlockListViewModel
import com.google.gson.Gson

@Composable
fun NonRefCheckBox(
    block: Block,
    blockListViewModel: BlockListViewModel,
    index: Int,
    position: Int,
    isActiveGroup: Boolean
) {
    val isRequired = block.required
    val gson = Gson()
    val existingData = blockListViewModel.getDataFromIndex(position, index)
    var selectedOptions = remember { mutableStateOf(existingData?.answer?.split(",")?.toSet() ?: emptySet()) }
//    val selectedOptions = remember { mutableStateOf<Set<Any>>(emptySet()) }

    val question = block.question?.alias ?: ""
    val blockId = block.id ?: ""

    LaunchedEffect (selectedOptions){
        val nonRefData = appSessionManager.getMobileVerificationData()
        if (!nonRefData.isNullOrEmpty()) {
            println("NonRefTextInput: $nonRefData")
            val numberCheckData: NumberCheckData? = gson.fromJson(nonRefData, NumberCheckData::class.java)

            if (numberCheckData != null && numberCheckData.information != null){
                for (dynamicInfo in numberCheckData.information) {
                    if (dynamicInfo.key == question) {
                        selectedOptions.value = dynamicInfo.value.split(",").toSet()
                    }
                }
            }
        }

        val surveyHistoryModel = SurveyHistoryModel(
            question = question,
            answer = selectedOptions.toString(),
            id = blockId
        )
        blockListViewModel.saveDataAtIndex(position, surveyHistoryModel)
    }


    Column {
        println("Block Id is: ${block.id}")
        Text(text = block.question!!.slug)

        Spacer(modifier = Modifier.height(8.dp))

        block.options?.forEach { option ->
            val isSelected = selectedOptions.value.contains(option.value)

            Surface(
                modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color.Blue)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.background(Color.White)
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { isChecked ->
                            selectedOptions.value = if (isChecked) {
                                selectedOptions.value + option.value
                            } else {
                                selectedOptions.value - option.value
                            }

                            val surveyHistoryModel = SurveyHistoryModel(
                                question = question,
                                answer = selectedOptions.toString(),
                                id = blockId
                            )

                            blockListViewModel.saveDataAtIndex(position, surveyHistoryModel)
                        },
                        enabled = isActiveGroup
                    )
                    Text(option.value)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}