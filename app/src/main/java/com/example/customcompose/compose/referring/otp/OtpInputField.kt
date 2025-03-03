package com.example.customcompose.compose.referring.otp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OtpInputField(otp: String, onOtpChange: (String) -> Unit) {
    val textFields = remember { List(6) { mutableStateOf("") } }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in textFields.indices) {
            OutlinedTextField(
                value = textFields[i].value,
                onValueChange = { newText ->
                    val oldText = textFields[i].value
                    if (newText.length <= 1) {
                        textFields[i].value = newText
                        val newOtp = textFields.joinToString("") { it.value }
                        onOtpChange(newOtp)

                        if (newText.isNotEmpty() && i < textFields.lastIndex) {
                            focusRequesters[i + 1].requestFocus()
                        }
                    }

                    if (oldText.isNotEmpty() && newText.isEmpty() && i > 0) {
                        textFields[i - 1].value = ""
                        focusRequesters[i - 1].requestFocus()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .focusRequester(focusRequesters[i])
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Backspace) {
                            if (textFields[i].value.isEmpty() && i > 0) {
                                textFields[i - 1].value = ""
                                focusRequesters[i - 1].requestFocus()
                            }
                        }
                        false
                    },
                textStyle = TextStyle(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                singleLine = true
            )
        }
    }
}