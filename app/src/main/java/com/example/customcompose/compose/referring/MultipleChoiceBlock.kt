package com.example.customcompose.compose.referring

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun MultipleChoiceBlock(block: Block, blockListViewModel: BlockListViewModel) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    val isSkippable = block.skip?.id != "-1"

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = block.question!!.slug)

        Spacer(modifier = Modifier.height(8.dp))

        val hasLongOption = block.options?.any { it.value.length > 15 } == true

        if (hasLongOption) {
            block.options?.forEach { option ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .clickable {
                            selectedOption = option.value
                            option.referTo?.id?.let { referToId ->
                                if (selectedOption != null){
                                    blockListViewModel.addBlockToTheList(referToId, option.referTo.group_no!!)
                                }
                            }
                        }
                        .background(
                            if (selectedOption == option.value) Color.LightGray else Color.Transparent
                        )
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
                                selectedOption = option.value
                                option.referTo?.id?.let { referToId ->
                                    if (selectedOption != null) {
                                        blockListViewModel.addBlockToTheList(referToId, option.referTo.group_no!!)
                                    }
                                }
                            }
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
                                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedOption = option.value
                                    option.referTo?.id?.let { referToId ->
                                        if (selectedOption != null) {
                                            blockListViewModel.addBlockToTheList(referToId, option.referTo.group_no!!)
                                        }
                                    }
                                }
                                .background(
                                    if (selectedOption == option.value) Color.LightGray else Color.Transparent
                                ),  // Change background if selected
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = option.value, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        if (isSkippable){
            Button(
                onClick = {
                    block.skip?.group_no?.let { groupId ->
                        block.skip.id.let { blockId ->
                            blockListViewModel.addBlockToTheList(blockId, groupId)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                )
            ) {
                Text("Skip")
            }
        }
    }
}
