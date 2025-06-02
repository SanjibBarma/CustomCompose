package com.example.customcompose.views.compose.referring.star_rating

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.customcompose.MyApplication.Companion.blockListViewModel
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun StarRatingBlock(
    block: Block,
    isActiveGroup: Boolean,
    destination: String
) {
    val context = LocalContext.current
    val currentBlockId = block.id ?: ""

    val savedRating = remember { mutableStateOf(block.surveyHistoryModel?.firstOrNull()?.answer?.toIntOrNull() ?: 0) }
    val ratingNumber = remember { mutableIntStateOf(savedRating.value) }

    val isSkippable = block.skip?.id != "-1"

    val question = block.question?.alias ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(block.question!!.slug)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                for (i in 1..5) {
                    Star(
                        isSelected = i <= ratingNumber.intValue,
                        onClick = {
                            ratingNumber.intValue = i

                            val surveyHistoryModel = SurveyHistoryModel(
                                question = question,
                                answer = ratingNumber.intValue.toString(),
                                id = currentBlockId
                            )
                            block.surveyHistoryModel = listOf(surveyHistoryModel)

                            block.referTo?.group_no?.let { groupId ->
                                block.referTo.id?.let { blockId ->
                                    if (destination == "mainSurvey") {
                                        blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId, block.position)
                                    } else {
                                        blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                                    }
                                }
                            }
//                        Toast.makeText(context, "Selected Rating: $i", Toast.LENGTH_SHORT).show()
                        },
                        isActiveGroup
                    )
                }
            }

            if (isSkippable) {
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {

                        val surveyHistoryModel = SurveyHistoryModel(
                            question = "",
                            answer = "",
                            id = currentBlockId
                        )
                        block.surveyHistoryModel = listOf(surveyHistoryModel)

                        block.skip?.id?.let { blockId ->
                            block.skip.group_no.let { groupId ->
                                if (destination == "mainSurvey") {
                                    blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId, block.position)
                                } else {
                                    blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isActiveGroup
                ) {
                    Text("Skip")
                }
            }
        }
    }
}