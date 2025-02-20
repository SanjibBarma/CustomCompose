package com.example.customcompose.compose.group

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.customcompose.compose.number_validation.NonRefCheckBox
import com.example.customcompose.compose.number_validation.NonRefContactNo
import com.example.customcompose.compose.number_validation.NonRefDate
import com.example.customcompose.compose.number_validation.NonRefDropdown
import com.example.customcompose.compose.number_validation.NonRefEmailInput
import com.example.customcompose.compose.number_validation.NonRefMultipleChoice
import com.example.customcompose.compose.number_validation.NonRefNumberInput
import com.example.customcompose.compose.number_validation.NonRefProductList
import com.example.customcompose.compose.number_validation.NonRefTextInput
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.viewmodel.BlockListViewModel
@Composable
fun NumberValidationGroup(
    blockListViewModel: BlockListViewModel,
    currentBlock: Block?,
    survey: SurveyDataModel,
    isActiveGroup: Boolean,
    position: Int
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            for ((index, block) in survey.blocks.withIndex()){
                when(block.type){
                    "dropdown" -> NonRefDropdown(block, blockListViewModel, index, position, isActiveGroup)
                    "date" -> NonRefDate(block, blockListViewModel, index, position, isActiveGroup)
                    "multipleChoice+icon" -> NonRefMultipleChoice(block, blockListViewModel, index, position, isActiveGroup)
                    "textInput" -> NonRefTextInput(block, blockListViewModel, index, position, isActiveGroup)
                    "checkbox" -> NonRefCheckBox(block, blockListViewModel, index, position, isActiveGroup)
                    "numberInput" -> NonRefNumberInput(block, blockListViewModel, index, position, isActiveGroup)
                    "multipleChoice" -> NonRefMultipleChoice(block, blockListViewModel, index, position, isActiveGroup)
                    "dropdown+condition" -> NonRefDropdown(block, blockListViewModel, index, position, isActiveGroup)
                    "emailInput" -> NonRefEmailInput(block, blockListViewModel, index, position, isActiveGroup)
                    "contactNo" -> NonRefContactNo(block, blockListViewModel, index, position, isActiveGroup)
                    "product" -> NonRefProductList(block, blockListViewModel, index, position, isActiveGroup)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {

                    blockListViewModel.addBlockToTheList(survey.jumping_logic[0].id, survey.jumping_logic[0].group_no)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = isActiveGroup
            ) {
                Text("Next")
            }
        }
    }
}
