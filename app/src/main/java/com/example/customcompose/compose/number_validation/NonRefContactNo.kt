package com.example.customcompose.compose.number_validation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NonRefContactNo(
    block: Block,
    blockListViewModel: BlockListViewModel,
    index: Int,
    position: Int,
    isActiveGroup: Boolean
) {
    val isRequired = block.required
    val question = block.question?.slug ?: ""
    val blockId = block.id ?: ""

    val existingData = blockListViewModel.getDataFromIndex(position, index)
    var phoneNumber by remember { mutableStateOf(existingData?.answer ?: "") }

    LaunchedEffect(phoneNumber) {
        val surveyHistoryModel = SurveyHistoryModel(
            question = question,
            answer = phoneNumber,
            id = blockId
        )
        blockListViewModel.saveDataAtIndex(position, index, surveyHistoryModel)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(text = block.question!!.slug)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                if (it.length <= 10) {  // Max 10 digits allowed
                    phoneNumber = it
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp).border(1.dp, color = Color.Gray),
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
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray,
                disabledTextColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone
            ),
            enabled = isActiveGroup
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}
