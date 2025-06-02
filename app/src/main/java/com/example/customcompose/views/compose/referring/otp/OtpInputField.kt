package com.example.customcompose.views.compose.referring.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun OtpInputField(
    otpLength: Int = 6,
    otp: String,
    onOtpChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                focusRequester.requestFocus()
                keyboardController?.show()
            }
    ) {
        BasicTextField(
            value = otp,
            onValueChange = {
                val filtered = it.take(otpLength)
                onOtpChange(filtered)
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Ascii),
            textStyle = TextStyle(color = Color.Transparent),
            cursorBrush = SolidColor(Color.Transparent),
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            for (i in 0 until otpLength) {
                val char = otp.getOrNull(i)?.toString() ?: ""
                val isCurrent = otp.length == i
                val showCursor = isCurrent || (otp.length == otpLength && i == otpLength - 1)

                var isCursorVisible by remember { mutableStateOf(true) }

                LaunchedEffect(showCursor) {
                    if (showCursor) {
                        while (true) {
                            isCursorVisible = !isCursorVisible
                            delay(500)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .border(1.dp, Color.Black)
                        .padding(2.dp)
                        .clickable {
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char,
                        style = TextStyle(color = Color.Black)
                    )

                    if (showCursor && isCursorVisible && char.isEmpty()) {
                        Spacer(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(Color.Black)
                        )
                    }
                }

                if (i != otpLength - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}
