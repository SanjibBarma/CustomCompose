package com.example.customcompose.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.customcompose.model.Block
import es.dmoral.toasty.Toasty

@Composable
fun TextInputBlock(block: Block, inputData: MutableMap<String, String>, isLast: Boolean, onNext: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    var isEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current

    val isSkippable = block.skip?.id != "-1"
    val validationRegex = block.validations?.regex

    // validate the text input based on the regex
    fun validateInput(input: String): Boolean {
        return if (validationRegex != null) {
            Regex(validationRegex).matches(input)
        } else {
            true // No validation if no regex is provided
        }
    }

    Column {
        Text(
            text = block.question!!.slug,
//            style = MaterialTheme.typography.titleMedium,
//            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = text,
            onValueChange = {
                text = it
                inputData[block.id] = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, Color.Gray),
            enabled = isEnabled
        )

        Spacer(modifier = Modifier.height(8.dp))


        Button(
            onClick = {
                isEnabled = false
                if (text.isBlank() && isSkippable) {
                    block.skip?.id?.let { onNext(it) }
                } else {
                    if (validateInput(text)) {
                        block.referTo?.id?.let { onNext(it) }
                    } else {
                        isEnabled = true
                        Toasty.warning(context, "Input valid ${block.question.slug}", Toasty.LENGTH_SHORT).show()
                    }
                }
            },
            enabled = (isSkippable || text.isNotBlank()) && isLast,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue, // Change the background color
                contentColor = Color.White // Change the text color
            )
        ) {
            Text("Next")
        }
    }
}
