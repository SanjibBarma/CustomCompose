package com.example.customcompose.compose

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.customcompose.model.Block

@Composable
fun OTPBlock(block: Block, isLast: Boolean, onNext: (String, String) -> Unit) {
    var otp by remember { mutableStateOf("") }
    var isEnabled by remember { mutableStateOf(true) }
    var showPopup by remember { mutableStateOf(true) }

    if (showPopup) {
        // Show a popup dialog
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = block.question!!.slug,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = otp,
                        onValueChange = { otp = it },
                        label = { Text("Enter OTP") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEnabled
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "OTP sent to your number. Please enter the OTP.",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    // Buttons
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { /* Handle resend OTP */ },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text("Resend")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = {
                                isEnabled = false
//                                block.referTo?.id?.let(onNext)
                                onNext(block.referTo?.id!!, block.referTo?.group_no!!)
                                showPopup = false
                            },
                            enabled = isLast && otp.length == 6,
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

//    Column {
//        Text(block.question.slug)
//        TextField(value = otp, onValueChange = { otp = it }, label = { Text("Enter OTP") }, modifier = Modifier.fillMaxWidth(), enabled = isEnabled)
//        Button(onClick = { isEnabled = false; block.referTo?.id?.let(onNext) }, enabled = isLast && otp.length == 6, modifier = Modifier.fillMaxWidth()) {
//            Text("Next")
//        }
//    }
}
