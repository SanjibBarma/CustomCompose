package com.example.customcompose.views.compose.referring

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.customcompose.MyApplication.Companion.blockListViewModel
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.ui.theme.OtpVerify
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun LookupBlock (
    block: Block,
    isActiveGroup: Boolean,
    destination: String
){
    val currentBlockId = block.id ?: ""
    val question = block.question?.alias ?: ""
    var showButton by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(currentBlockId) {
        val surveyHistoryModel = SurveyHistoryModel(
            question = question,
            answer = "yes",
            id = currentBlockId
        )
        block.surveyHistoryModel= listOf(surveyHistoryModel)

        block.referTo?.group_no?.let { groupId ->
            block.referTo.id?.let { nextBlockId ->
                if (destination == "mainSurvey"){
                    blockListViewModel.addBlockToTheSurveyFlow(nextBlockId, groupId, block.position)
                }else{
                    blockListViewModel.addBlockToTheCheckList(nextBlockId, groupId)
                }
            }
        }

        //first do the api call from here initially
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "All data is updated successfully!.",
                    color = OtpVerify,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            if (showButton){
                Button(
                    onClick = {
                        val surveyHistoryModel = SurveyHistoryModel(
                            question = question,
                            answer = "yes",
                            id = currentBlockId
                        )
                        block.surveyHistoryModel= listOf(surveyHistoryModel)

                        block.referTo?.group_no?.let { groupId ->
                            block.referTo.id?.let { nextBlockId ->
                                if (destination == "mainSurvey"){
                                    blockListViewModel.addBlockToTheSurveyFlow(nextBlockId, groupId, block.position)
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
                    )
                ) {
                    Text("Reload")
                }
            }
        }
    }
}