package com.example.customcompose.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EditTextCompose(id: Int, isDisabled: Boolean, onTextChange: (String) -> Unit, onNextClick: () -> Unit) {
    var text by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Item #$id", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = text,
                onValueChange = {
                    text = it
                    onTextChange(it)
                },
                label = { Text("Enter Text") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isDisabled
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onNextClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = text.isNotBlank() && !isDisabled
            ) {
                Text("Next")
            }
        }
    }
}
