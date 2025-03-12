@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.customcompose.views

import android.util.Log
import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import kotlinx.coroutines.launch

import androidx.compose.material3.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.customcompose.MyApplication
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.compose.route_plan.RoutePlanView
import com.example.customcompose.compose.SubmitButton
import com.example.customcompose.compose.dialog.ExitDialog
import com.example.customcompose.compose.group.CheckGroupOrBlock
import com.example.customcompose.compose.referring.LocationBlock
import com.example.customcompose.model.RoutePlanData
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.model.SurveyModel
import com.example.customcompose.ui.theme.DimBackground
import com.example.customcompose.viewmodel.BlockListViewModel
import com.example.customcompose.viewmodel.LoginViewModel
import com.example.customcompose.viewmodel.SurveyFlowViewModel
import com.example.customcompose.views.SurveyDataManager.surveyData
import com.example.customcompose.views.SurveyDataManager.surveyDataFlow
import com.google.gson.Gson

@Composable
fun DynamicScreen(
    blockListViewModel: BlockListViewModel,
    loginViewModel: LoginViewModel,
    numberValidationViewModel: SurveyFlowViewModel,
    navController: NavHostController,
    routePlanList: List<RoutePlanData>
) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var showDialog by remember { mutableStateOf(false) }

    onBackPressedDispatcher?.addCallback {
        showDialog = true
        Log.d("DynamicScreen", "Back button pressed on DynamicScreen")
    }

    if (showDialog){
        ExitDialog(
            showDialog = showDialog,
            onCancelClick = { showDialog = false },
            onExitClick = {
                navController.popBackStack()
                blockListViewModel.clearRouteList()
                blockListViewModel.clearParentBlockList()
                appSessionManager.setMobileVerificationData("")
                showDialog = false
            }
        )
    }

    val context = LocalContext.current
//    val appSessionManager = MyApplication.appSessionManager
    val parentSurveyList by blockListViewModel.parentSurveyBlockList.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isSubmitted by blockListViewModel.isSubmitted.collectAsState()
    val isProgressLoading by blockListViewModel.isProgressLoading.collectAsState()
    val isRoutePlanShow by blockListViewModel.isRoutePlan.collectAsState()

    val gson = Gson()

    appSessionManager.getCampaignId()?.let { camId ->
        loginViewModel.fetchSurveyDataByIds(appSessionManager.getBrId().toString(), camId)
    }

    val surveyDataState = loginViewModel.localSurveyData.observeAsState()

//    var surveyData: SurveyModel? = null
    var surveyName by remember { mutableStateOf("") }

    LaunchedEffect(surveyDataState.value) {
        surveyDataState.value?.campData?.let { campData ->
            surveyData = gson.fromJson(campData, SurveyModel::class.java)
            surveyData?.let {
                val routePlanLocal = it.route_plan
                surveyDataFlow = it.survey_flow
                surveyName = it.name
                Log.d("routePlanFromLocal", routePlanLocal.toString())

                routePlanLocal?.get(0)?.let { routePlan ->
                    blockListViewModel.showRoutePlanView()
                    blockListViewModel.clearRouteList()
                    appSessionManager.setMobileVerificationData("")
                    blockListViewModel.addNextRoutePlanData(routePlan.type_slug, routePlanLocal, blockListViewModel.routeParentList.value.size)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(surveyName) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
            ) {

                LaunchedEffect(parentSurveyList.size) {
                    if (parentSurveyList.isNotEmpty()) {
                        coroutineScope.launch {
                            listState.animateScrollToItem(parentSurveyList.size)
                        }
                    }
                }

                LazyColumn(state = listState, modifier = Modifier.imePadding()) {
                    item {
                        if (parentSurveyList.isNotEmpty()) {
                            LocationBlock(blockListViewModel, false)
                        }
                    }

                    items(parentSurveyList) { childView ->
                        val isCurrentGroupActive =
                            !isSubmitted && parentSurveyList.lastOrNull()?.group == childView.group
                        val position = childView.position
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

        if (isRoutePlanShow) {
            RoutePlanView(blockListViewModel, surveyDataFlow!!, navController, onDismiss = { blockListViewModel.hideRoutePlanView() })
        }

        if (isProgressLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DimBackground)
                    .pointerInput(Unit) { }
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(100.dp)
                        .width(100.dp),
                    strokeWidth = 8.dp
                )
            }
        }
    }
}

object SurveyDataManager {
    var surveyDataFlow: List<SurveyDataModel>? = null
    var surveyData: SurveyModel? = null
}

