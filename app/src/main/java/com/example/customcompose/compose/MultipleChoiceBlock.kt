package com.example.customcompose.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.customcompose.model.Block

@Composable
fun MultipleChoiceBlock(block: Block, inputData: MutableMap<String, String>, isLast: Boolean, onNext: (String) -> Unit) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isEnabled by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = block.question!!.slug,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val hasLongOption = block.options?.any { it.value.length > 15 } == true

        if (hasLongOption) {
            block.options?.forEach { option ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .clickable(enabled = isEnabled && selectedOption == null) {
                            selectedOption = option.value
                            option.referTo?.id?.let { referToId ->
                                if (isLast && selectedOption != null) onNext(referToId)
                            }
                            inputData[block.id] = selectedOption ?: ""
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption == option.value,
                            onClick = {
                                if (selectedOption == null) {
                                    selectedOption = option.value
                                    inputData[block.id] = selectedOption ?: ""
                                    option.referTo?.id?.let { referToId ->
                                        if (isLast && selectedOption != null) onNext(referToId)
                                    }
                                }
                            },
                            enabled = selectedOption == null
                        )
                        Text(
                            text = option.value,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 300.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(block.options ?: emptyList()) { option ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(80.dp)
                                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                .clickable(enabled = isEnabled && selectedOption == null) {
                                    selectedOption = option.value
                                    inputData[block.id] = selectedOption ?: ""
                                    option.referTo?.id?.let { referToId ->
                                        if (isLast && selectedOption != null) onNext(referToId)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = option.value, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
