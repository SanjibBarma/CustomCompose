package com.example.customcompose.compose.referring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.customcompose.R
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun StarRatingBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    position: Int,
    isActiveGroup: Boolean
) {
    val context = LocalContext.current
    val ratingNumber = remember { mutableIntStateOf(0) }
    val isSkippable = block.skip?.id != "-1"

    val question = block.question?.slug ?: ""
    val blockId = block.id ?: ""

    Column {
        Text(block.question!!.slug)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            //if list started from 0, then add +1 with the size
            for (i in 1..5) {
                Star(
                    isSelected = i <= ratingNumber.intValue,
                    onClick = {
                        ratingNumber.intValue = i

                        val surveyHistoryModel = listOf(
                            SurveyHistoryModel(
                                question = question,
                                answer = ratingNumber.intValue.toString(),
                                id = blockId
                            )
                        )
                        blockListViewModel.saveData(position, surveyHistoryModel)

                        block.referTo?.group_no?.let { groupId ->
                            block.referTo.id?.let { blockId ->
                                blockListViewModel.addBlockToTheList(blockId, groupId)
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

                    val surveyHistoryModel = listOf(
                        SurveyHistoryModel(
                            question = "",
                            answer = "",
                            id = blockId
                        )
                    )
                    blockListViewModel.saveData(position, surveyHistoryModel)

                    block.skip?.id?.let { blockId ->
                        block.skip.group_no.let { groupId ->
                            blockListViewModel.addBlockToTheList(blockId, groupId)
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

@Composable
fun Star(isSelected: Boolean, onClick: () -> Unit, isActiveGroup: Boolean) {
    val starIcon = if (isSelected) {
        R.drawable.ic_star_on
    } else {
        R.drawable.ic_star_off
    }
    val starColor = if (isSelected) Color.Yellow else Color.Gray

    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        enabled = isActiveGroup
    ) {
        Icon(
            painter = painterResource(id = starIcon),
            contentDescription = "Star",
            tint = starColor
        )
    }
}
