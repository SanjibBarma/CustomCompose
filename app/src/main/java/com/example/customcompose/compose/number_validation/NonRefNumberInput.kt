package com.example.customcompose.compose.number_validation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NonRefNumberInput(
    block: Block,
    blockListViewModel: BlockListViewModel,
    index: Int,
    position: Int,
    isActiveGroup: Boolean
) {

    val isRequired = block.required
    val existingData = blockListViewModel.getDataFromIndex(position, index)
    var text by remember { mutableStateOf(existingData?.answer ?: "")  }
    val question = block.question?.slug ?: ""
    val blockId = block.id ?: ""

    LaunchedEffect(text) {
        val surveyHistoryModel = SurveyHistoryModel(
            question = question,
            answer = text,
            id = blockId
        )

        blockListViewModel.saveDataAtIndex(position, surveyHistoryModel)
    }


    Column (
        modifier = Modifier
            .fillMaxWidth()
    ){
        println("Block Id is: ${block.id}")
        Text(text = block.question!!.slug)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
            },
            modifier = Modifier.fillMaxWidth().height(56.dp).border(1.dp, color = Color.Gray),
            singleLine = true,
            shape = RoundedCornerShape(4.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = if (isActiveGroup) Color.White else Color.LightGray,
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            enabled = isActiveGroup
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}