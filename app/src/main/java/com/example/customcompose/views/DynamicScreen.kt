@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.customcompose.views

import android.util.Log
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.customcompose.compose.route_plan.RoutePlanView
import com.example.customcompose.compose.SubmitButton
import com.example.customcompose.compose.group.CheckGroupOrBlock
import com.example.customcompose.compose.referring.LocationBlock
import com.example.customcompose.helper.AppSessionManager
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.model.SurveyModel
import com.example.customcompose.viewmodel.BlockListViewModel
import com.example.customcompose.viewmodel.LoginViewModel
import com.example.customcompose.viewmodel.NumberValidationViewModel
import com.example.customcompose.views.SurveyDataManager.surveyDataModel
import com.google.gson.Gson

@Composable
fun DynamicScreen(
    blockListViewModel: BlockListViewModel,
    surveyDataModelList: List<SurveyDataModel>,
    loginViewModel: LoginViewModel,
    numberValidationViewModel: NumberValidationViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val appSessionManager = remember { AppSessionManager(context) }
    val surveyViewListItem by blockListViewModel.parentSurveyBlockList.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isSubmitted by blockListViewModel.isSubmitted.collectAsState()
    val isRoutePlanShow by blockListViewModel.isRoutePlan.collectAsState()

    val gson = Gson()
    val surveyDataState = loginViewModel.localSurveyData.observeAsState()
    val surveyData: SurveyModel = gson.fromJson(surveyDataState.value?.campData, SurveyModel::class.java)

    val routePlanLocal = surveyData.route_plan
//    val surveyDataModel = surveyData.survey_flow
    SurveyDataManager.surveyDataModel = surveyData.survey_flow

    Log.d("routePlanFromLocal", routePlanLocal.toString())

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
//                        blockListViewModel.addNextRoutePlanData(routePlanList[0].type_slug, routePlanList, blockListViewModel.routeParentList.value.size)
                        routePlanLocal?.get(0)?.let { blockListViewModel.addNextRoutePlanData(it.type_slug, routePlanLocal, blockListViewModel.routeParentList.value.size) }
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
                            surveyDataModel!!,
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

object SurveyDataManager {
    var surveyDataModel: List<SurveyDataModel>? = null
}

