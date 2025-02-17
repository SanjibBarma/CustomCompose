package com.example.customcompose.compose

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

@Composable
fun DropdownBlock(block: Block, inputData: MutableMap<String, String>, isLast: Boolean, onNext: (String, String) -> Unit) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isEnabled by remember { mutableStateOf(true) }
    var selectedReferTo by remember { mutableStateOf<String?>(null) }
    var selectedReferToGroup by remember { mutableStateOf<String?>(null) }

    Column {
        Text(block.question!!.slug)
        Spacer(modifier = Modifier.height(8.dp))

        // Box for the dropdown
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .padding(8.dp)
                .clickable(enabled = isEnabled) { isDropdownExpanded = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedOption ?: "Select an option") // Display selected option or placeholder text
                Icon(
                    imageVector = Icons.Default.ArrowDropDown, // Downward arrow icon
                    contentDescription = "Dropdown Arrow", // Accessibility description
                    modifier = Modifier.size(24.dp) // Set icon size
                )
            }
        }

        // Dropdown menu
        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }
        ) {
            block.options?.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.value) },
                    onClick = {
                        selectedOption = option.value
                        selectedReferTo = option.referTo?.id
                        selectedReferToGroup = option.referTo?.group_no
                        isDropdownExpanded = false
                        inputData[block.id] = selectedOption ?: ""
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Next button
        Button(
            onClick = {
                isEnabled = false;
//                selectedReferTo?.let(onNext())
                onNext(selectedReferTo!!, selectedReferToGroup!!)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isLast && selectedOption != null && isEnabled
        ) {
            Text("Next")
        }
    }
}
