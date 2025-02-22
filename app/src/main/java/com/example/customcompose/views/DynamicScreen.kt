@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.customcompose.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.customcompose.compose.group.NonReferringGroup
import com.example.customcompose.compose.group.NumberValidationGroup
import com.example.customcompose.compose.group.ReferringGroup
import kotlinx.coroutines.launch

import androidx.compose.material3.*
import com.example.customcompose.compose.SubmitButton
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun DynamicScreen(
    blockListViewModel: BlockListViewModel,
    surveyDataModelList: List<SurveyDataModel>
) {
    val surveyViewListItem by blockListViewModel.surveyBlockListItem.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isSubmitted by blockListViewModel.isSubmitted.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Survey Name") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .imePadding()
                .padding(paddingValues)
        ) {
            if (surveyViewListItem.isEmpty()) {
                Button(
                    onClick = {
                        blockListViewModel.addBlockToTheSurveyFlow(surveyDataModelList[0].blocks[0].id!!, surveyDataModelList[0].group)
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 100.dp)
                ) {
                    Text("Start")
                }
            }

            LaunchedEffect(surveyViewListItem.size) {
                if (surveyViewListItem.isNotEmpty()) {
                    coroutineScope.launch {
                        listState.animateScrollToItem(surveyViewListItem.size - 1)
                    }
                }
            }

            LazyColumn(state = listState) {
                items(surveyViewListItem) { childView ->
                    val isCurrentGroupActive = !isSubmitted && surveyViewListItem.lastOrNull()?.group == childView.group
                    val position = childView.position
                    println("Type Name: ${childView.type}")
                    println("BlockData: $childView")
                    when (childView.type) {
                        "referring" -> ReferringGroup(blockListViewModel, childView, isCurrentGroupActive, "mainSurvey")
                        "non-referring" -> NonReferringGroup(blockListViewModel, childView, position, isCurrentGroupActive, "mainSurvey")
                        "numbervalidation" -> NumberValidationGroup(blockListViewModel, childView, position, isCurrentGroupActive, "mainSurvey")
                    }
                }

                if (isSubmitted) {
                    item {
//                        Button(
//                            onClick = {
//                                // blockListViewModel.addBlockToTheList("100", "7")
//                                Toast.makeText(context, "Submit", Toast.LENGTH_SHORT).show()
//                            },
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Text("Submit")
//                        }
                        SubmitButton(blockListViewModel)
                    }
                }
            }
        }
    }
}

