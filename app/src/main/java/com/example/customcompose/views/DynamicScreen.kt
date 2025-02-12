package com.example.customcompose.views

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyDataModel
import com.google.gson.Gson

@Composable
fun DynamicScreen(surveyDataModel: List<SurveyDataModel>) {
    val inputData = remember { mutableStateMapOf<String, String>() }
    val visitedGroups = remember { mutableStateListOf<List<Block>>() }
    val allBlocks = surveyDataModel.flatMap { it.blocks }.associateBy { it.id }

    var currentGroup by remember { mutableStateOf(surveyDataModel.firstOrNull()?.blocks) }
    var showSubmitButton by remember { mutableStateOf(false) }

    LaunchedEffect(currentGroup) {
        currentGroup?.let { group ->
            if (group !in visitedGroups) {
                visitedGroups.add(group)
                // Set showSubmitButton to true when all groups are visited
                if (visitedGroups.size == surveyDataModel.size) {
                    showSubmitButton = true
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()).imePadding()
    ) {
        visitedGroups.forEach { group ->
            when (group.firstOrNull()?.type) {
                "referring" -> ReferringGroup(group) { navigateToNextGroup(group, surveyDataModel) { nextGroup -> currentGroup = nextGroup } }
                "non-referring" -> NonReferringGroup(group, inputData) { navigateToNextGroup(group, surveyDataModel) { nextGroup -> currentGroup = nextGroup } }
                "number-validation" -> NumberValidationGroup(group, inputData) { navigateToNextGroup(group, surveyDataModel) { nextGroup -> currentGroup = nextGroup } }
                else -> ReferringGroup(group) { navigateToNextGroup(group, surveyDataModel) { nextGroup -> currentGroup = nextGroup } }
            }
        }

        if (showSubmitButton) {
            SubmitButton(inputData, visitedGroups.flatten())
        }
    }
}

fun navigateToNextGroup(
    currentGroup: List<Block>,
    surveyDataModel: List<SurveyDataModel>,
    setCurrentGroup: (List<Block>) -> Unit
) {
    val referToGroupNo = currentGroup.firstOrNull()?.referTo?.group_no
    val nextGroup = surveyDataModel.flatMap { it.blocks }
        .filter { it.id == referToGroupNo }

    if (nextGroup.isNotEmpty()) {
        setCurrentGroup(nextGroup)
        return
    }

    val defaultNextGroup = surveyDataModel.getOrNull(1)?.blocks
    if (defaultNextGroup != null) {
        setCurrentGroup(defaultNextGroup)
    }
}


@Composable
fun SubmitButton(inputData: MutableMap<String, String>, visitedBlocks: List<Block>) {
    Button(
        onClick = {
            val gson = Gson()
            val json = gson.toJson(inputData)
            Log.d("SURVEY_DATA", json)
        },
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Text("Submit")
    }
}

@Composable
fun ReferringGroup(group: List<Block>, onNext: () -> Unit) {
    val block = group.firstOrNull() ?: return
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Referring: ${block.question?.slug}")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onNext) {
                Text("Next")
            }
        }
    }
}


@Composable
fun NonReferringGroup(group: List<Block>, inputData: MutableMap<String, String>, onNext: () -> Unit) {
    val block = group.firstOrNull() ?: return // First block in the group

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Non-Referring: ${block.question?.slug}")
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = inputData[block.id] ?: "", onValueChange = { inputData[block.id] = it })
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onNext) {
                Text("Next")
            }
        }
    }
}

@Composable
fun NumberValidationGroup(group: List<Block>, inputData: MutableMap<String, String>, onNext: () -> Unit) {
    val block = group.firstOrNull() ?: return // First block in the group

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Number Validation: ${block.question?.slug}")
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = inputData[block.id] ?: "", onValueChange = { inputData[block.id] = it })
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onNext) {
                Text("Next")
            }
        }
    }
}


