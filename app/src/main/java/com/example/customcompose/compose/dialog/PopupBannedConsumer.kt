package com.example.customcompose.compose.dialog

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.customcompose.R
import com.example.customcompose.model.DynamicInfoConModel

@Composable
fun PopupBannedConsumer (
    dynmcInfoConModelList: List<DynamicInfoConModel>,
    messages: List<String>,
    onDismiss: () -> Unit,
    goToNextPage: () -> Unit
){
    var msg by remember { mutableStateOf("")  }


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

                        if (messages.isNotEmpty()){
//                            for (message in messages){
//                                msg = msg + message + "\n"
//                            }
                            msg = messages[0]
                        }

                        Text(
                            text = msg.ifEmpty { "This consumer was connected before in this campaign" },
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, top = 40.dp),
                            fontFamily = FontFamily.SansSerif,
                            color = Color.Black,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn (
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            items(dynmcInfoConModelList){ data ->
                                Text(text = ("${data.key}  :  ${data.value}"))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Consumer is not eligible for this contact.",
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, top = 40.dp),
                            fontFamily = FontFamily.SansSerif,
                            color = Color.Black,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
//                            Button(
//                                onClick = {
//                                    goToNextPage ()
//                                    onDismiss()
//                                },
//                                modifier = Modifier.width(120.dp),
//                                colors = ButtonDefaults.buttonColors(Color(0xFF6200EE))
//                            ) {
//                                Text(text = "Yes", color = Color.White)
//                            }

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
                painter = painterResource(id = R.drawable.popup_icon_banned_contact),
                contentDescription = "Popup Icon",
                modifier = Modifier
                    .height(90.dp)
                    .width(90.dp)
                    .align(Alignment.TopCenter)
            )
        }
    }
}