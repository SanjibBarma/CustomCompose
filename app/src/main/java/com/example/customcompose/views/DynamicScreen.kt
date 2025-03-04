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
import com.example.customcompose.compose.SubmitButton
import com.example.customcompose.compose.group.CheckGroupOrBlock
import com.example.customcompose.compose.referring.LocationBlock
import com.example.customcompose.model.RoutePlanData
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.viewmodel.BlockListViewModel
import com.example.customcompose.viewmodel.NumberValidationViewModel

@Composable
fun DynamicScreen(
    blockListViewModel: BlockListViewModel,
    surveyDataModelList: List<SurveyDataModel>,
    routePlanList: List<RoutePlanData>,
    numberValidationViewModel: NumberValidationViewModel
) {
    val surveyViewListItem by blockListViewModel.surveyBlockListItem.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isSubmitted by blockListViewModel.isSubmitted.collectAsState()
    val isRoutePlanShow by blockListViewModel.isRoutePlan.collectAsState()

    val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjUwNjIsInVzZXJuYW1lIjoiYnJ0ZXN0aW1zbEBlY3JtLWltc2wiLCJzY2hlbWEiOiJlY3JtIiwicGxhdGZvcm0iOjExNiwidXNlcl90eXBlIjoiZmYiLCJyb2xlIjoxLCJyb2xlX25hbWUiOiJSQSIsImVtYWlsIjoiYnJ0ZXN0aW1zbEBnbWFpbC5jb20iLCJyZXBvcnR0b19pZCI6MjUwNjAsImFnZW5jeSI6MSwib3JnX2luZm8iOnsiaWQiOjEsIm5hbWUiOiJCcml0aXNoIEFtZXJpY2FuIFRvYmFjY28gQmFuZ2xhZGVzaCIsInRhZyI6ImVjcm0iLCJkZXNjcmlwdGlvbiI6IkJyaXRpc2ggQW1lcmljYW4gVG9iYWNjbyBCYW5nbGFkZXNoIiwiYWRkcmVzcyI6IkRoYWthIiwicGhvbmVfbnVtYmVycyI6WyIxNzQ2MDk0MzQyIl0sImNvbnRhY3RfcGVyc29ucyI6W10sImlzX2RlbGV0ZWQiOmZhbHNlLCJjcmVhdGVkX2F0IjoiMjAyMS0wOC0zMVQwMzo1MjowOS42NTVaIiwidXBkYXRlZF9hdCI6IjIwMjEtMDgtMzFUMDM6NTI6MDkuNjU1WiIsInNpbmdsZV9kZXZpY2UiOnRydWUsInRoZW1lIjp7InByaWFtcnlfY29sb3IiOiIjMEQyQjYzIiwib3JnX2xvZ28iOiJEZXZlbG9wbWVudC91YmwvSW1hZ2VzL0xvZ28vOTY3MjIwOGYtODEzNS00MjdhLWEyZjItNTJkZWNmODkxMDY2LnBuZyJ9fSwiZGV2aWNlX2lkIjoiODQ0NDIzODAyNjE5NGE1YyIsImlhdCI6MTc0MDk4NDIzOCwiZXhwIjoxNzQxMDEzMDM4fQ.vKdHL6IBcsEX_yxA5dT6rUKcyqdCO0kw1NIWMV6ch_Q "


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
                        blockListViewModel.clearRouteList()
                        blockListViewModel.addNextRoutePlanData(routePlanList[0].type_slug, routePlanList, routePlanList.size+1)
                        blockListViewModel.showRoutePlanView()

//                        numberValidationViewModel.getAchievementData("bearer $token", "127")
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

                    CheckGroupOrBlock(blockListViewModel, childView, isCurrentGroupActive, position, "mainSurvey", numberValidationViewModel)
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

