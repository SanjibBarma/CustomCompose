package com.example.customcompose.compose.referring

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun CheckboxBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    position: Int,
    isActiveGroup: Boolean
) {
    val isSkippable = block.skip?.id != "-1"

    // Get existing data
    val existingData = blockListViewModel.getData(position)
    val selectedOptions = remember { mutableStateOf(existingData.firstOrNull()?.answer?.split(",")?.toSet() ?: emptySet()) }

    val question = block.question?.slug ?: ""
    val blockId = block.id ?: ""

    Column {
        Text(text = question)

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

                        val surveyHistoryModel = listOf(
                            SurveyHistoryModel(
                                question = "",
                                answer = "",
                                id = blockId
                            )
                        )
                        blockListViewModel.saveData(position, surveyHistoryModel)

                        block.skip?.group_no?.let { groupId ->
                            block.skip.id.let { blockId ->
                                blockListViewModel.addBlockToTheList(blockId, groupId)
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
                Spacer(modifier = Modifier.width(8.dp))
            }

            Button(
                onClick = {
                    val answer = selectedOptions.value.joinToString(",") // Convert Set to Comma-Separated String
                    val surveyHistoryModel = listOf(
                        SurveyHistoryModel(
                            question = question,
                            answer = answer,
                            id = blockId
                        )
                    )
                    blockListViewModel.saveData(position, surveyHistoryModel)

                    block.referTo?.group_no?.let { groupId ->
                        block.referTo.id?.let { nextBlockId ->
                            blockListViewModel.addBlockToTheList(nextBlockId, groupId)
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
                Text("Next")
            }
        }
    }
}
