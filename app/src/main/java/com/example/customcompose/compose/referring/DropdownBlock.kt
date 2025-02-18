package com.example.customcompose.compose.referring

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.customcompose.model.Block
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun DropdownBlock(block: Block, blockListViewModel: BlockListViewModel) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val isSkippable = block.skip?.id != "-1"

    Column {
        Text(block.question!!.slug)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .padding(8.dp)
                .clickable { isDropdownExpanded = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedOption ?: "Select an option")
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }
        ) {
            block.options?.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.value) },
                    onClick = {
                        option.referTo?.id?.let { blockId ->
                            option.referTo.group_no?.let { groupId ->
                                blockListViewModel.addBlockToTheList(blockId, groupId)
                            }
                        }
                        selectedOption = option.value
                        isDropdownExpanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isSkippable) {
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
