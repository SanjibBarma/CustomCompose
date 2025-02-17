package com.example.customcompose.compose

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.customcompose.model.Block
import com.example.customcompose.model.JumpingLogic
import com.example.customcompose.model.SurveyDataModel
import com.google.gson.Gson
import es.dmoral.toasty.Toasty


@Composable
fun DynamicScreenTest(surveyDataModelList: List<SurveyDataModel>) {
    val inputData = remember { mutableStateMapOf<String, String>() }
    val loadedGroups = remember { mutableStateListOf<SurveyDataModel>() }
    val visitedBlocks = remember { mutableStateMapOf<String, List<Block>>() }
    var showSubmit by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    var previousBlockCount by remember { mutableStateOf(0) }

    val allBlocks by remember(loadedGroups, visitedBlocks) {
        derivedStateOf {
            loadedGroups.flatMap { group ->
                visitedBlocks[group.group].orEmpty().map { block -> group to block }
            }
        }
    }

    LaunchedEffect(allBlocks.size) {
        if (allBlocks.size > previousBlockCount && previousBlockCount > 0) {
            lazyListState.animateScrollToItem(allBlocks.lastIndex)
        }
        previousBlockCount = allBlocks.size
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            if (loadedGroups.isEmpty()) {
                Button(
                    onClick = {
                        // Define your starting point
                        val initialGroupId = "7"
                        val initialBlockId = "100" // Replace with actual ID

                        surveyDataModelList.find { it.group == initialGroupId }?.let { group ->
                            // Find the specific block in the group
                            val targetBlock = group.blocks.find { it.id == initialBlockId }

                            // Add group if not loaded
                            if (!loadedGroups.contains(group)) {
                                loadedGroups.add(group)
                            }

                            // Initialize with specific block or fallback to first
                            visitedBlocks[group.group] = listOf(
                                targetBlock ?: group.blocks.first()
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Start Survey") }
            }
        }

        items(
            items = allBlocks,
            key = { (group, block) -> "${group.group}-${block.id}" }
        ) { (group, block) ->
            when (group.type) {
                "referring" -> ReferringGroupBlock(
                    block = block,
                    group = group,
                    inputData = inputData,
                    onNext = { targetBlockId, targetGroupId ->
                        handleNavigation(
                            targetBlockId,
                            targetGroupId,
                            group,
                            surveyDataModelList,
                            loadedGroups,
                            visitedBlocks,
                            { showSubmit = it }
                        )
                    }
                )
                "non-referring" -> NonReferringGroup(
                    group = group,
                    inputData = inputData,
                    onNext = { targetBlockId, targetGroupId ->
                        handleNavigation(
                            targetBlockId,
                            targetGroupId,
                            group,
                            surveyDataModelList,
                            loadedGroups,
                            visitedBlocks,
                            { showSubmit = it }
                        )
                    }
                )
                "numbervalidation" -> NumberValidationGroup(
                    group = group,
                    inputData = inputData,
                    onNext = { targetBlockId, targetGroupId ->
                        handleNavigation(
                            targetBlockId,
                            targetGroupId,
                            group,
                            surveyDataModelList,
                            loadedGroups,
                            visitedBlocks,
                            { showSubmit = it }
                        )
                    }
                )
            }
        }

        if (showSubmit) {
            item { SubmitButton(inputData, loadedGroups.flatMap { it.blocks }) }
        }
    }
}

@Composable
fun ReferringGroupBlock(block: Block, group: SurveyDataModel, inputData: MutableMap<String, String>, onNext: (String, String) -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            when (block.type) {
                "audio_start" -> TextInputBlock(block, inputData, true) { id, groupId -> onNext(id, groupId) }
                "textInput" -> TextInputBlock(block, inputData, true) { id, groupId -> onNext(id, groupId) }
                "terms" -> TermsBlock(block, true) { id, groupId -> onNext(id, groupId) }
                "otp" -> OTPBlock(block, true) { id, groupId -> onNext(id, groupId) }
                "dropdown" -> DropdownBlock(block, inputData, true) { id, groupId -> onNext(id, groupId) }
                "multipleChoice" -> MultipleChoiceBlock(block, inputData, true) { id, groupId -> onNext(id, groupId) }
                else -> {
                    // Show error message in UI
                    Text("Unsupported block type: ${block.type}", color = Color.Red)

                    // Show toast notification
                    LaunchedEffect(block.type) {
                        Toasty.error(
                            context,
                            "View not found for block type: ${block.type}",
                            Toasty.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}

fun handleNavigation(targetBlockId: String, targetGroupId: String, currentGroup: SurveyDataModel, surveyDataList: List<SurveyDataModel>, loadedGroups: MutableList<SurveyDataModel>, visitedBlocks: MutableMap<String, List<Block>>, showSubmit: (Boolean) -> Unit) {
    if (targetGroupId == "submit" && targetBlockId == "submit") {
        showSubmit(true)
        return
    }

    val targetGroup = surveyDataList.find { it.group == targetGroupId }
    val targetBlock = targetGroup?.blocks?.find { it.id == targetBlockId }

    // Check if we have valid target group AND block
    val validTarget = when {
        targetGroup != null && targetBlock != null -> true
        else -> false
    }

    if (!validTarget) {
        // Fallback to current group's jumping logic
        currentGroup.jumping_logic.firstOrNull()?.let { jump ->
            handleJumpLogic(
                jump = jump,
                surveyDataList = surveyDataList,
                loadedGroups = loadedGroups,
                visitedBlocks = visitedBlocks,
                showSubmit = showSubmit
            )
        }
        return
    }

    // If we have valid target, proceed with navigation
    if (targetGroup != null) {
        when {
            targetGroup.group == currentGroup.group -> {
                // Same group: append block
                val currentList = visitedBlocks[currentGroup.group].orEmpty()
                if (currentList.isEmpty() || currentList.last() != targetBlock) {
                    visitedBlocks[currentGroup.group] = (currentList + targetBlock) as List<Block>
                }
            }

            else -> {
                // Different group: initialize group and add block
                if (!loadedGroups.contains(targetGroup)) {
                    if (targetGroup != null) {
                        loadedGroups.add(targetGroup)
                    }
                }
                val targetGroupBlocks = visitedBlocks[targetGroup?.group].orEmpty()
                if (targetGroupBlocks.isEmpty() || targetGroupBlocks.last() != targetBlock) {
                    visitedBlocks[targetGroup.group] = (targetGroupBlocks + targetBlock) as List<Block>
                }
            }
        }
    }
}

private fun handleJumpLogic(jump: JumpingLogic, surveyDataList: List<SurveyDataModel>, loadedGroups: MutableList<SurveyDataModel>, visitedBlocks: MutableMap<String, List<Block>>, showSubmit: (Boolean) -> Unit) { when (jump.group_no) {
        "submit" -> showSubmit(true)
        else -> {
            surveyDataList.find { it.group == jump.group_no }?.let { newGroup ->
                // Add group if not already loaded
                if (!loadedGroups.contains(newGroup)) {
                    loadedGroups.add(newGroup)
                }

                // Add first block if not already present
                val newGroupBlocks = visitedBlocks[newGroup.group].orEmpty()
                val firstBlock = newGroup.blocks.first()
                if (newGroupBlocks.isEmpty() || newGroupBlocks.last() != firstBlock) {
                    visitedBlocks[newGroup.group] = newGroupBlocks + firstBlock
                }
            }
        }
    } }

@Composable
fun SubmitButton(inputData: MutableMap<String, String>, flatten: List<Block>) {
    Button(
        onClick = {
            val gson = Gson()
            val json = gson.toJson(inputData)
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
fun TextInputBlock(block: Block, inputData: MutableMap<String, String>, isLast: Boolean, onNext: (String, String) -> Unit) {
    var text by remember { mutableStateOf("") }
    var isEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current

    val isSkippable = block.skip?.id != "-1"
    val validationRegex = block.validations?.regex

    fun validateInput(input: String): Boolean {
        return if (validationRegex != null) {
            Regex(validationRegex).matches(input)
        } else {
            true
        }
    }

    Column {
        Text(text = block.question!!.slug)

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
                    block.skip?.id?.let { onNext(block.skip.id, block.skip.group_no) }
                } else {
                    if (validateInput(text)) {
                        block.referTo?.id?.let { onNext(block.referTo.id, block.referTo.group_no!!) }
                    } else {
                        isEnabled = true
                        Toasty.warning(context, "Input valid ${block.question.slug}", Toasty.LENGTH_SHORT).show()
                    }
                }
            },
            enabled = (isSkippable || text.isNotBlank()) && isLast,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            )
        ) {
            Text("Next")
        }
    }
}

@Composable
fun NonReferringGroup(group: SurveyDataModel, inputData: MutableMap<String, String>, onNext: (String, String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Non-Referring: ")
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = "", onValueChange = {  })
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {onNext(group.jumping_logic[0].id, group.jumping_logic[0].group_no)}) {
                Text("Next")
            }
        }
    }
}

@Composable
fun NumberValidationGroup(group: SurveyDataModel, inputData: MutableMap<String, String>, onNext: (String, String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Number Validation: ")
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value =  "", onValueChange = {  })
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {onNext(group.jumping_logic[0].id, group.jumping_logic[0].group_no)}) {
                Text("Next")
            }
        }
    }
}