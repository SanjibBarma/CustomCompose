package com.example.customcompose.views.compose.referring.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.customcompose.helper.CommonUtils.generateOtp
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.ui.theme.OtpVerify
import com.example.customcompose.viewmodel.BlockListViewModel
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.delay

@Composable
fun OTPBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String
) {
    var otp by remember { mutableStateOf("") }
    var sentOtpTrack by remember { mutableStateOf(1) }
    val context = LocalContext.current
    val currentBlockId = block.id ?: ""
    val isSkippable = block.skip?.id != "-1"
    val isBypass = block.validations?.bypass
//    val isBypass = false
    val showPopup by blockListViewModel.isShowOtp.collectAsState()
    var checkInitialOtp by remember { mutableStateOf(block.surveyHistoryModel?.firstOrNull() == null) }

    var generatedOtp by remember { mutableStateOf(if (isBypass == true) "123456" else generateOtp()) }
    var countdown by remember { mutableStateOf(90) }
    var isResendVisible by remember { mutableStateOf(false) }
    var toastShown by remember { mutableStateOf(false) }
    var showSendButton by remember { mutableStateOf(false) }

    LaunchedEffect(countdown) {
        while (countdown > 0) {
            delay(90000L)
            countdown--
        }
        isResendVisible = true
    }


    LaunchedEffect(Unit) {

        if (isBypass == true && !toastShown && checkInitialOtp) {
            Toasty.warning(context, "Bypass is true. Not sending otp.", Toasty.LENGTH_SHORT).show()
            generatedOtp = "123456"
            toastShown = true
            blockListViewModel.showOtpPopup()
            showSendButton = false
            return@LaunchedEffect
        }

        if (block.validations?.server == true && block.validations.device == true) {
            if (sentOtpTrack == 0) {
                Toasty.warning(context, "Server sms send", Toasty.LENGTH_SHORT).show()
                sentOtpTrack = 1
            } else if (sentOtpTrack == 1) {
                Toasty.warning(context, "Device sms send", Toasty.LENGTH_SHORT).show()
                sentOtpTrack = 2
            } else {
                Toasty.warning(context, "Server sms send", Toasty.LENGTH_SHORT).show()
                sentOtpTrack = 1
            }
        } else if (!block.validations?.server!! && block.validations?.device!!) {
            Toasty.warning(context, "Device sms send", Toasty.LENGTH_SHORT).show()
        }else if (block.validations.server!! && !block.validations.device!!){
            Toasty.warning(context, "Server sms send", Toasty.LENGTH_SHORT).show()
        } else {
            if (checkInitialOtp){
                Toasty.warning(context, "Internet OTP configuration", Toasty.LENGTH_SHORT).show()
                return@LaunchedEffect
            }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (showSendButton) {
                Button(
                    onClick = {
                        countdown = 90
                        if (isBypass == true && !toastShown && checkInitialOtp) {
                            Toasty.warning(context, "Bypass is true. Not sending otp.", Toasty.LENGTH_SHORT).show()
                            generatedOtp = "123456"
                            toastShown = true
                            blockListViewModel.showOtpPopup()
                            showSendButton = false
                            return@Button
                        }

                        if (block.validations?.server == true && block.validations.device == true) {
                            if (sentOtpTrack == 0) {
                                Toasty.warning(context, "Server sms send", Toasty.LENGTH_SHORT).show()
                                sentOtpTrack = 1
                            } else if (sentOtpTrack == 1) {
                                Toasty.warning(context, "Device sms send", Toasty.LENGTH_SHORT).show()
                                sentOtpTrack = 2
                            } else {
                                Toasty.warning(context, "Server sms send", Toasty.LENGTH_SHORT).show()
                                sentOtpTrack = 1
                            }
                        } else if (!block.validations?.server!! && block.validations?.device!!) {
                            Toasty.warning(context, "Device sms send", Toasty.LENGTH_SHORT).show()
                        } else if (block.validations.server!! && !block.validations.device!!){
                            Toasty.warning(context, "Server sms send", Toasty.LENGTH_SHORT).show()
                        } else {
                            if (checkInitialOtp){
                                Toasty.warning(context, "Internet OTP configuration", Toasty.LENGTH_SHORT).show()
                                return@Button
                            }
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Send Otp", fontSize = 12.sp)
                }
            }else if (!showPopup){
                Text(
                    text = "Verified successfully.",
                    color = OtpVerify,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }

    if (showPopup){
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Number Verification",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OtpInputField(otp = otp, onOtpChange = { otp = it })
                    Spacer(modifier = Modifier.height(8.dp))

                    println("Generated OTP: $generatedOtp")

                    Text(
                        text = "OTP sent to your number. Please enter the OTP.",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontSize = 12.sp
                    )

                    Text(
                        text = if (countdown > 0) "Input OTP in $countdown seconds" else "Please send otp again",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (isSkippable) {
                            Button(
                                onClick = {
                                    var surveyHistoryModel = SurveyHistoryModel(
                                        question = "",
                                        answer = "",
                                        id = currentBlockId
                                    )
                                    block.surveyHistoryModel= listOf(surveyHistoryModel)

                                    block.skip?.group_no?.let { groupId ->
                                        block.skip.id.let { nextBlockId ->
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
                                ),
                                enabled = isActiveGroup
                            ) {
                                Text("Skip")
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        if (isResendVisible) {
                            Button(
                                onClick = {
                                    countdown = 90
                                    isResendVisible = false

                                    if (isBypass == true && !toastShown && checkInitialOtp) {
                                        Toasty.warning(context, "Bypass is true. Not sending otp.", Toasty.LENGTH_SHORT).show()
                                        generatedOtp = "123456"
                                        toastShown = true
                                        blockListViewModel.showOtpPopup()
                                        showSendButton = false
                                        return@Button
                                    }

                                    if (block.validations?.server == true && block.validations.device == true) {
                                        if (sentOtpTrack == 0) {
                                            Toasty.warning(context, "Server sms send", Toasty.LENGTH_SHORT).show()
                                            sentOtpTrack = 1
                                        } else if (sentOtpTrack == 1) {
                                            Toasty.warning(context, "Device sms send", Toasty.LENGTH_SHORT).show()
                                            sentOtpTrack = 2
                                        } else {
                                            Toasty.warning(context, "Server sms send", Toasty.LENGTH_SHORT).show()
                                            sentOtpTrack = 1
                                        }
                                    } else if (!block.validations?.server!! && block.validations?.device!!) {
                                        Toasty.warning(context, "Device sms send", Toasty.LENGTH_SHORT).show()
                                    } else if (block.validations.server!! && !block.validations.device!!){
                                        Toasty.warning(context, "Server sms send", Toasty.LENGTH_SHORT).show()
                                    } else {
                                        if (checkInitialOtp){
                                            Toasty.warning(context, "Internet OTP configuration", Toasty.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                    }
                                },
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text("Resend", fontSize = 12.sp)
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (otp == generatedOtp) {
                                        val surveyHistoryModel = SurveyHistoryModel(
                                            question = block.question?.slug ?: "",
                                            answer = "Yes",
                                            id = currentBlockId
                                        )
                                        block.surveyHistoryModel = listOf(surveyHistoryModel)

                                        if (destination == "mainSurvey") {
                                            blockListViewModel.addBlockToTheSurveyFlow(block.referTo?.id!!, block.referTo.group_no!!, block.position)
                                        } else {
                                            blockListViewModel.addBlockToTheCheckList(block.referTo?.id!!, block.referTo.group_no!!)
                                        }

                                        blockListViewModel.hideOtpPopup()
                                    } else {
                                        Toasty.warning(context, "Invalid OTP", Toasty.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text("Verify & Proceed", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clickable {
                            blockListViewModel.hideOtpPopup()
                            showSendButton = true
                            checkInitialOtp = true
                            toastShown = false
                        },
                    tint = Color.Black
                )
            }
        }
    }
}