@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.customcompose.views

import android.widget.Toast
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
import com.example.customcompose.viewmodel.BlockListViewModel
import com.example.customcompose.compose.group.ReferringGroup
import kotlinx.coroutines.launch

import androidx.compose.material3.*
import com.example.customcompose.model.SurveyDataModel

@Composable
fun DynamicScreen(
    blockListViewModel: BlockListViewModel,
    surveyDataModelList: List<SurveyDataModel>
) {
    val viewListItem by blockListViewModel.blockListItem.collectAsState()
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
            if (viewListItem.isEmpty()) {
                Button(
                    onClick = {
                        blockListViewModel.addBlockToTheList(surveyDataModelList[0].blocks[0].id, surveyDataModelList[0].group)
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 100.dp)
                ) {
                    Text("Start")
                }
            }

            LaunchedEffect(viewListItem.size) {
                if (viewListItem.isNotEmpty()) {
                    coroutineScope.launch {
                        listState.animateScrollToItem(viewListItem.size - 1)
                    }
                }
            }

            LazyColumn(state = listState) {
                items(viewListItem) { childView ->
                    val survey = childView.group
                    val block = childView.block
                    val isCurrentGroupActive = !isSubmitted && viewListItem.lastOrNull()?.group?.group == survey.group
                    val position = childView.position
                    when (survey.type) {
                        "referring" -> ReferringGroup(blockListViewModel, block, survey, isCurrentGroupActive, position)
                        "non-referring" -> NonReferringGroup(blockListViewModel, block, survey, isCurrentGroupActive, position)
                        "numbervalidation" -> NumberValidationGroup(blockListViewModel, block, survey, isCurrentGroupActive, position)
                    }
                }

                if (isSubmitted) {
                    item {
                        Button(
                            onClick = {
                                // blockListViewModel.addBlockToTheList("100", "7")
                                Toast.makeText(context, "Submit", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Submit")
                        }
                    }
                }
            }
        }
    }
}

