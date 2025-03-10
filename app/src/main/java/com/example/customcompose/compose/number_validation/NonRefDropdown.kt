package com.example.customcompose.compose.number_validation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun NonRefDropdown(
    block: Block,
    blockListViewModel: BlockListViewModel,
    index: Int,
    position: Int,
    isActiveGroup: Boolean
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val isRequired = block.required

    val existingData = blockListViewModel.getDataFromIndex(position, index)
    var selectedOption by remember { mutableStateOf(existingData?.answer ?: "") }
    val question = block.question?.alias ?: ""
    val blockId = block.id ?: ""

    LaunchedEffect (selectedOption){
        val surveyHistoryModel = SurveyHistoryModel(
            question = question,
            answer = selectedOption,
            id = blockId
        )
        blockListViewModel.saveDataAtIndex(position, surveyHistoryModel)
    }

    Column {
        println("Block Id is: ${block.id}")
        Text(block.question!!.slug)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .padding(8.dp)
                .clickable (enabled = isActiveGroup){ isDropdownExpanded = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedOption)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier.size(24.dp)
                )
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
