package com.example.customcompose.compose.number_validation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun NonRefMultipleChoice(
    block: Block,
    blockListViewModel: BlockListViewModel,
    index: Int,
    position: Int,
    isActiveGroup: Boolean
) {
    val isRequired = block.required

    val existingData = blockListViewModel.getDataFromIndex(position, index)
    var selectedOption by remember { mutableStateOf(existingData?.answer ?: "") }
    val question = block.question?.slug ?: ""
    val blockId = block.id ?: ""

    LaunchedEffect (selectedOption){
        val surveyHistoryModel = SurveyHistoryModel(
            question = question,
            answer = selectedOption,
            id = blockId
        )
        blockListViewModel.saveDataAtIndex(position, surveyHistoryModel)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        println("Block Id is: ${block.id}")
        Text(text = block.question!!.slug)

        Spacer(modifier = Modifier.height(8.dp))

        block.options?.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                    .clickable (enabled = isActiveGroup){
                        selectedOption = option.value

                        val surveyHistoryModel = SurveyHistoryModel(
                            question = question,
                            answer = selectedOption,
                            id = blockId
                        )

                        blockListViewModel.saveDataAtIndex(position, surveyHistoryModel)
                    }
                    .background(
                        if (selectedOption == option.value) Color.LightGray else Color.Transparent
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedOption == option.value,
                        onClick = {
                            selectedOption = option.value

                            val surveyHistoryModel = SurveyHistoryModel(
                                question = question,
                                answer = selectedOption,
                                id = blockId
                            )

                            blockListViewModel.saveDataAtIndex(position, surveyHistoryModel)

                        },
                        enabled = isActiveGroup
                    )
                    Text(
                        text = option.value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}