@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.customcompose.views

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

import androidx.compose.material3.*
import com.example.customcompose.compose.RoutePlanView
import com.example.customcompose.compose.group.CheckGroupOrBlock
import com.example.customcompose.compose.SubmitButton
import com.example.customcompose.compose.referring.LocationBlock
import com.example.customcompose.model.RoutePlanData
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun DynamicScreen(
    blockListViewModel: BlockListViewModel,
    surveyDataModelList: List<SurveyDataModel>,
    routePlanList: List<RoutePlanData>
) {
    val surveyViewListItem by blockListViewModel.surveyBlockListItem.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isSubmitted by blockListViewModel.isSubmitted.collectAsState()
    val isRoutePlanShow by blockListViewModel.isRoutePlan.collectAsState()

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
                .padding(paddingValues)
        ) {
            if (surveyViewListItem.isEmpty() && !isRoutePlanShow) {
                Button(
                    onClick = {
//                        sharedPrefHelper.savePreviousGroupId("")
//                        sharedPrefHelper.clearCheckList()
//                        blockListViewModel.addBlockToTheSurveyFlow(surveyDataModelList[0].blocks[0].id!!, surveyDataModelList[0].group)
                        blockListViewModel.clearRouteList()
                        blockListViewModel.addNextRoutePlanData(routePlanList[0].type_slug, routePlanList, routePlanList.size+1)
                        blockListViewModel.showRoutePlanView()
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

            LazyColumn(state = listState, modifier = Modifier.imePadding()) {

                item{
                    if(isRoutePlanShow){
                        RoutePlanView(
                            blockListViewModel,
                            surveyDataModelList,
                            onDismiss = {
                                blockListViewModel.hideRoutePlanView()
                                //blockListViewModel.clearRouteList()
                            }
                        )
                    }

                    if (surveyViewListItem.isNotEmpty()){
                        LocationBlock(blockListViewModel, false)
                    }
                }

                items(surveyViewListItem) { childView ->
                    val isCurrentGroupActive = !isSubmitted && surveyViewListItem.lastOrNull()?.group == childView.group
                    val position = childView.position
                    println("Type Name: ${childView.type}")
                    println("BlockData: $childView")

                    CheckGroupOrBlock(blockListViewModel, childView, isCurrentGroupActive, position, "mainSurvey")
                }

                if (isSubmitted) {
                    item {
                        SubmitButton(blockListViewModel)
                    }
                }
            }
        }
    }
}

