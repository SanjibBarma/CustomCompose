package com.example.customcompose.views

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.HorizontalAlign
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyDataModel
import com.google.gson.Gson

@Composable
fun DynamicScreen(surveyDataModel: SurveyDataModel) {
    val inputData = remember { mutableStateMapOf<String, String>() }
    val visitedBlocks = remember { mutableStateListOf<Block>() }
    val allBlocks = surveyDataModel.blocks.associateBy { it.id }
    val scrollState = rememberScrollState()

    var currentBlockId by remember { mutableStateOf(surveyDataModel.blocks.firstOrNull()?.id) }
    var showSubmitButton by remember { mutableStateOf(false) }

    LaunchedEffect(currentBlockId) {
        currentBlockId?.let { id ->
            allBlocks[id]?.let { block ->
                if (block !in visitedBlocks) visitedBlocks.add(block)
            } ?: run { showSubmitButton = true }
        }
    }

    LaunchedEffect(visitedBlocks.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
            .imePadding()
    ) {
        visitedBlocks.forEachIndexed { index, block ->
            val isCurrentBlock = index == visitedBlocks.lastIndex

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                elevation = CardDefaults.cardElevation(if (isCurrentBlock) 4.dp else 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrentBlock) Color.White else Color(Color.LightGray.value)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    when (block.type) {
                        "textInput" -> TextInputBlock(block, inputData, isCurrentBlock, onNext = { nextId -> navigateToNextBlock(nextId, { currentBlockId = it }) })
                        "terms" -> TermsBlock(block, isCurrentBlock, onNext = { nextId -> navigateToNextBlock(nextId, { currentBlockId = it }) })
                        "otp" -> OTPBlock(block, isCurrentBlock, onNext = { nextId -> navigateToNextBlock(nextId, { currentBlockId = it }) })
                        "dropdown" -> DropdownBlock(block, inputData, isCurrentBlock, onNext = { nextId -> navigateToNextBlock(nextId, { currentBlockId = it }) })
                        "multipleChoice" -> MultipleChoiceBlock(block, inputData, isCurrentBlock, onNext = { nextId -> navigateToNextBlock(nextId, { currentBlockId = it }) })
                    }
                }
            }
        }

        if (showSubmitButton) {
            SubmitButton(inputData, visitedBlocks)
        }
    }
}

fun navigateToNextBlock(nextId: String, setCurrentBlockId: (String) -> Unit) {
    setCurrentBlockId(nextId)
}

@Composable
fun SubmitButton(inputData: MutableMap<String, String>, visitedBlocks: List<Block>) {
    Button(
        onClick = {
            val surveyResults = visitedBlocks.mapNotNull { block ->
                if (block.skip?.id != "-1" && (inputData[block.id].isNullOrBlank())) {
                    return@mapNotNull null
                }

                val answer = when (block.type) {
                    "otp" -> "yes"
                    "terms" -> "yes"
                    "dropdown" -> inputData[block.id] ?: ""
                    "multipleChoice" -> inputData[block.id] ?: ""
                    "textInput" -> inputData[block.id] ?: ""
                    else -> inputData[block.id] ?: ""
                }

                mapOf("block_id" to block.id, "question" to block.question.slug, "answer" to answer)
            }

            val gson = Gson()
            val json = gson.toJson(surveyResults)
            Log.d("SURVEY_DATA", json)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Submit")
    }
}

@Composable
fun TextInputBlock(block: Block, inputData: MutableMap<String, String>, isLast: Boolean, onNext: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    var isEnabled by remember { mutableStateOf(true) }

    val isSkippable = block.skip?.id != "-1"

    Column {
        Text(
            text = block.question.slug,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

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

        Button(
            onClick = {
                isEnabled = false
                if (text.isBlank() && isSkippable) {
                    block.skip?.id?.let { onNext(it) }
                } else {
                    block.referTo?.id?.let { onNext(it) }
                }
            },
            enabled = (isSkippable || text.isNotBlank()) && isLast,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next")
        }
    }
}

@Composable
fun TermsBlock(block: Block, isLast: Boolean, onNext: (String) -> Unit) {
    var isChecked by remember { mutableStateOf(false) }
    var isEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = block.question.slug, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(12.dp))

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
            .padding(8.dp)
            .verticalScroll(rememberScrollState())) {
            Column {
                block.validations?.terms?.forEach { term ->
                    Text(text = term, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isChecked, onCheckedChange = { isChecked = it })
            Text(text = "I agree to the terms and conditions", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { isEnabled = false; block.referTo?.id?.let(onNext) }, enabled = isChecked && isEnabled) {
            Text("I Agree")
        }
    }
}

@Composable
fun OTPBlock(block: Block, isLast: Boolean, onNext: (String) -> Unit) {
    var otp by remember { mutableStateOf("") }
    var isEnabled by remember { mutableStateOf(true) }

    Column {
        Text(block.question.slug)
        TextField(value = otp, onValueChange = { otp = it }, label = { Text("Enter OTP") }, modifier = Modifier.fillMaxWidth(), enabled = isEnabled)
        Button(onClick = { isEnabled = false; block.referTo?.id?.let(onNext) }, enabled = isLast && otp.length == 6, modifier = Modifier.fillMaxWidth()) {
            Text("Next")
        }
    }
}

@Composable
fun DropdownBlock(block: Block, inputData: MutableMap<String, String>, isLast: Boolean, onNext: (String) -> Unit) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isEnabled by remember { mutableStateOf(true) }
    var selectedReferTo by remember { mutableStateOf<String?>(null) }

    Column {
        Text(block.question.slug)
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .padding(8.dp)
            .clickable(enabled = isEnabled) { isDropdownExpanded = true }) {
            Text(selectedOption ?: "Select an option")
        }

        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }
        ) {
            block.options?.forEach { option ->
                DropdownMenuItem(text = { Text(option.value) }, onClick = {
                    selectedOption = option.value
                    selectedReferTo = option.referTo?.id
                    isDropdownExpanded = false
                    inputData[block.id] = selectedOption ?: ""
                })
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { isEnabled = false; selectedReferTo?.let(onNext) },
            modifier = Modifier.fillMaxWidth(),
            enabled = isLast && selectedOption != null && isEnabled
        ) {
            Text("Next")
        }
    }
}

@Composable
fun MultipleChoiceBlock(block: Block, inputData: MutableMap<String, String>, isLast: Boolean, onNext: (String) -> Unit) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isEnabled by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = block.question.slug,
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
