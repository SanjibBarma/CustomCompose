package com.example.customcompose.views.compose.referring.url

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun UrlBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String
) {
    val currentBlockId = block.id ?: ""

    val stringUrl = block.options?.get(0)!!.value
    val isSkippable = block.skip?.id != "-1"
    var showDialog by remember { mutableStateOf(false) }


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
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                    .padding(16.dp)
                    .clickable(enabled = isActiveGroup) {
                        showDialog = true

                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringUrl,
                    style = TextStyle(color = Color.Blue),
                    modifier = Modifier.clickable(enabled = isActiveGroup) {
                        showDialog = true
                    }
                )
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

            if (showDialog) {
                WebViewDialog(
                    block = block,
                    url = stringUrl,
                    blockListViewModel,
                    destination,
                    onClose = {
                        showDialog = false
                    }
                )
            }
        }
    }
}