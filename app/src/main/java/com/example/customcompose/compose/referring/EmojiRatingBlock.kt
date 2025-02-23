package com.example.customcompose.compose.referring

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customcompose.R
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun EmojiRatingBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String
) {
    val isSkippable = block.skip?.id != "-1"

    val currentBlockId = block.id ?: ""

    val emojiImageArray = intArrayOf(
        R.drawable.ic_angry,
        R.drawable.ic_sad,
        R.drawable.ic_confuse,
        R.drawable.ic_happy,
        R.drawable.ic_great
    )

    val emojiTitles = listOf(
        "Terrible",
        "Bad",
        "Okay",
        "Good",
        "Great"
    )

    var selectedEmojiIndex by remember { mutableStateOf(block.surveyHistoryModel?.firstOrNull()?.answer ?: "") }
    val selectedIndex = emojiTitles.indexOf(selectedEmojiIndex).takeIf { it != -1 }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(block.question!!.slug)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                emojiImageArray.forEachIndexed { index, emojiResource ->
                    EmojiBox(
                        emojiImageResource = emojiResource,
                        title = emojiTitles[index],
                        isSelected = selectedIndex == index,
                        onSelect = {
                            selectedEmojiIndex = emojiTitles[index]
                            val surveyHistoryModel = SurveyHistoryModel(
                                question = block.question.slug ?: "",
                                answer = emojiTitles[index],
                                id = currentBlockId
                            )
                            block.surveyHistoryModel = listOf(surveyHistoryModel)

                            block.referTo?.id?.let { blockId ->
                                block.referTo.group_no?.let { groupId ->
                                    if (destination == "mainSurvey") {
                                        blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId)
                                    } else {
                                        blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                                    }
                                }
                            }
                        },
                        isActiveGroup
                    )
                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isSkippable) {
                Button(
                    onClick = {
                        val surveyHistoryModel = SurveyHistoryModel(
                            question = "",
                            answer = "",
                            id = currentBlockId
                        )
                        block.surveyHistoryModel = listOf(surveyHistoryModel)

                        block.skip?.group_no?.let { groupId ->
                            block.skip.id.let { blockId ->
                                if (destination == "mainSurvey") {
                                    blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId)
                                } else {
                                    blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                                }
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
            }
        }
    }
}

@Composable
fun EmojiBox(
    emojiImageResource: Int,
    title: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    isActiveGroup: Boolean
) {
    val backgroundColor = if (isSelected) Color.LightGray else Color.White
    val borderColor = if (isSelected) Color.Black else Color.Transparent

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(8.dp),
        onClick = {
            onSelect()
        },
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
        enabled = isActiveGroup
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .height(70.dp)
                .width(50.dp)
                .background(backgroundColor)
        ) {
            Image(
                painter = painterResource(id = emojiImageResource),
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = Color.Black,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



