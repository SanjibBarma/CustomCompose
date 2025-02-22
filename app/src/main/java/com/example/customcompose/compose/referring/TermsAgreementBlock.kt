package com.example.customcompose.compose.referring

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun TermsAgreementBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String
) {
    val currentBlockId = block.id ?: ""
    val existingData = if (destination == "mainSurvey") {
        blockListViewModel.getData(currentBlockId)
    } else {
        blockListViewModel.getDataFromCheckList(currentBlockId)
    }

    var isChecked by remember { mutableStateOf(existingData?.firstOrNull()?.answer == "Yes") }
//    var isChecked by remember { mutableStateOf(false) }

    val question = block.question?.slug ?: ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = block.question!!.slug)

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                block.validations?.terms?.forEach { term ->
                    Text(text = term, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isChecked, onCheckedChange = { isChecked = it }, enabled = isActiveGroup)
            Text(text = "I agree to the terms and conditions", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                //isEnabled = false;
                val surveyHistoryModel = listOf(
                    SurveyHistoryModel(
                        question = question,
                        answer = "Yes",
                        id = currentBlockId
                    )
                )
                if (destination == "mainSurvey"){
                    blockListViewModel.saveData(currentBlockId, surveyHistoryModel)
                    blockListViewModel.addBlockToTheSurveyFlow(block.referTo?.id!!, block.referTo.group_no!!)
                }else{
                    blockListViewModel.saveDataToCheckList(currentBlockId, surveyHistoryModel)
                    blockListViewModel.addBlockToTheCheckList(block.referTo?.id!!, block.referTo.group_no!!)
                }

            },
            enabled = isChecked && isActiveGroup
        ) {
            Text("I Agree")
        }
    }
}
