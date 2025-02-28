package com.example.customcompose.compose.referring

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel
import com.google.gson.Gson
import es.dmoral.toasty.Toasty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextBlock(block: Block, blockListViewModel: BlockListViewModel, isActiveGroup: Boolean, destination: String) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isSkippable = block.skip?.id != "-1"
    val validationRegex = block.validations?.regex
    val focusManager = LocalFocusManager.current
    val currentBlockId = block.id ?: ""

    var text by remember { mutableStateOf(block.surveyHistoryModel?.firstOrNull()?.answer ?: "")  }
    val question = block.question?.slug ?: ""

    fun validateInput(input: String): Boolean {
        return if (validationRegex != null) {
            Regex(validationRegex).matches(input)
        } else {
            true
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Column (
                modifier = Modifier
                    .background(if (isActiveGroup) Color.White else Color(0x80CCCCCC))
            ){
                Text(text = question)

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
                        keyboardType = KeyboardType.Text
                    ),
                    enabled = isActiveGroup
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (isSkippable) {
                        Button(
                            onClick = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                var surveyHistoryModel = SurveyHistoryModel(
                                    question = "",
                                    answer = "",
                                    id = currentBlockId
                                )
                                block.surveyHistoryModel= listOf(surveyHistoryModel)

                                block.skip?.group_no?.let { groupId ->
                                    block.skip.id.let { nextBlockId ->
                                        if (destination == "mainSurvey"){
                                            blockListViewModel.addBlockToTheSurveyFlow(nextBlockId, groupId)
                                        }else{
                                            blockListViewModel.addBlockToTheCheckList(nextBlockId, groupId)
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
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            if (text.isNotBlank()) {
                                if (validateInput(text)) {
                                    val surveyHistoryModel = SurveyHistoryModel(
                                        question = question,
                                        answer = text,
                                        id = currentBlockId
                                    )
                                    block.surveyHistoryModel= listOf(surveyHistoryModel)

                                    block.referTo?.group_no?.let { groupId ->
                                        block.referTo.id?.let { nextBlockId ->
                                            if (destination == "mainSurvey"){
                                                blockListViewModel.addBlockToTheSurveyFlow(nextBlockId, groupId)
                                            }else{
                                                blockListViewModel.addBlockToTheCheckList(nextBlockId, groupId)
                                            }
                                        }
                                    }
                                } else {
                                    Toasty.warning(context, "Invalid input for ${block.question?.slug}", Toasty.LENGTH_SHORT).show()
                                }
                            } else {
                                Toasty.warning(context, "Please enter a valid ${block.question?.slug}", Toasty.LENGTH_SHORT).show()
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
}

