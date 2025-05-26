package com.example.customcompose.views.compose.helper_compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.customcompose.R

@Composable
fun PopupFreshConsumer(
    onDismiss: () -> Unit,
    goToNextPage: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Transparent)
        ) {

            Column {
                Spacer(modifier = Modifier.height(35.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Consumer is eligible for this contact",
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, top = 40.dp),
                            fontFamily = FontFamily.SansSerif,
                            color = Color.Black,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    goToNextPage ()
                                    onDismiss()

                                },
                                modifier = Modifier.width(120.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFF6200EE))
                            ) {
                                Text(text = "Yes", color = Color.White)
                            }

                            OutlinedButton(
                                onClick = { onDismiss() },
                                modifier = Modifier.width(120.dp)
                            ) {
                                Text(text = "Cancel", color = Color.Red)
                            }

                        }
                    }
                }
            }

            Image(
                painter = painterResource(id = R.drawable.popup_icon_new_contact),
                contentDescription = "Popup Icon",
                modifier = Modifier
                    .height(90.dp)
                    .width(90.dp)
                    .align(Alignment.TopCenter)
            )
        }
    }
}