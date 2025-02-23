package com.example.customcompose.compose.referring

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel
import es.dmoral.toasty.Toasty

@Composable
fun OTPBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String
) {
    var otp by remember { mutableStateOf("") }
    val context = LocalContext.current
    val currentBlockId = block.id ?: ""

    var showPopup by remember { mutableStateOf(block.surveyHistoryModel?.firstOrNull() == null) }
//    var showPopup by rememberSaveable { mutableStateOf(true) }

    val question = block.question?.slug ?: ""

    if (showPopup) {
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = block.question!!.slug)
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = otp,
                        onValueChange = { otp = it },
                        label = { Text("Enter OTP") },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "OTP sent to your number. Please enter the OTP.",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = {
//                                val surveyHistoryModel = listOf(
//                                    SurveyHistoryModel(
//                                        question = "",
//                                        answer = "",
//                                        id = blockId
//                                    )
//                                )
//                                blockListViewModel.saveData(block.id, surveyHistoryModel)
//                                blockListViewModel.addBlockToTheSurveyFlow(block.skip?.id!!, block.skip.group_no)
                            },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text("Resend")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = {
                                val surveyHistoryModel =  SurveyHistoryModel(
                                    question = question,
                                    answer = "Yes",
                                    id = currentBlockId
                                )
                                if (destination == "mainSurvey") {
                                    block.surveyHistoryModel = listOf(surveyHistoryModel)
                                    blockListViewModel.addBlockToTheSurveyFlow(block.referTo?.id!!, block.referTo.group_no!!)
                                }else{
                                    blockListViewModel.saveHistoryForChecklist(currentBlockId, surveyHistoryModel)
                                    blockListViewModel.addBlockToTheCheckList(block.referTo?.id!!, block.referTo.group_no!!)
                                }

//                                Toasty.success(context, "blockId ${block.referTo?.id!!} groupId: ${block.referTo.group_no!!}")

                                showPopup = false // Dismiss dialog
                            },
                            enabled = otp.length == 6,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text("Verify")
                        }
                    }
                }
            }
        }
    }

    if (!showPopup) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Verified successfully.",
                    color = Color.Green,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}