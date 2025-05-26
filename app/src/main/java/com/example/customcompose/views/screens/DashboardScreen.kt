package com.example.customcompose.views.screens

import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.MyApplication.Companion.blockListViewModel
import com.example.customcompose.MyApplication.Companion.dashboardViewModel
import com.example.customcompose.MyApplication.Companion.loginViewModel
import com.example.customcompose.helper.UIState
import com.example.customcompose.navigation.Screen
import com.example.customcompose.views.compose.CustomAppBar
import com.example.customcompose.views.compose.helper_compose.KeepScreenOnEffect
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DashboardScreen(navController: NavHostController) {
    BackHandler {  }
    KeepScreenOnEffect()

    val context = LocalContext.current
    val campaignListDataState = loginViewModel.campaignListData.observeAsState(initial = UIState.Loading)
    val extraServiceDataState = dashboardViewModel.extraServiceData.observeAsState(initial = UIState.Loading)
    val targetAchievementDataState = dashboardViewModel.targetAchievementData.observeAsState(initial = UIState.Loading)
    val extraServiceMap = HashMap<String, Any>()
    val gson = Gson()

    extraServiceMap.apply {
        extraServiceMap.put("device_id", Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) )
    }
    extraServiceMap.apply {
        extraServiceMap.put("supervisor", false)
    }

    LaunchedEffect(Unit) {
        appSessionManager.getSessionToken()?.let {token ->
            dashboardViewModel.getExtraServices("bearer $token", extraServiceMap)
        }
    }

    Scaffold(
        topBar = {
            CustomAppBar(
                "Dashboard",
                null
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when (val state = campaignListDataState.value) {
                is UIState.Error -> {
                    Toasty.error(context, state.exception.message.toString(), Toasty.LENGTH_SHORT).show()
                }
                is UIState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is UIState.Success -> {
                    val selectedItemId = remember { mutableStateOf(state.data.selectedCamp ?: 0) }

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.data.data) { campaign ->
                            val isSelected = selectedItemId.value == campaign.id

                            Box(
                                modifier = Modifier
                                    .height(56.dp)
                                    .border(
                                        1.dp,
                                        if (isSelected) Color.Gray else Color.LightGray,
                                        RoundedCornerShape(15.dp)
                                    )
                                    .background(if (isSelected) Color.LightGray else Color.White)
                                    .clickable {

                                        blockListViewModel.clearRouteList()

                                        selectedItemId.value = campaign.id
                                        state.data.selectedCamp = campaign.id

                                        appSessionManager.setCampaignId(campaign.id.toString())

                                        //using CoroutineScope to await and async one by one
                                        CoroutineScope(Dispatchers.IO).launch {
                                            val resultSurveyData = async {
                                                loginViewModel.fetchSurveyDataByIds(
                                                    appSessionManager.getBrId().toString(),
                                                    campaign.id.toString()
                                                )
                                            }

                                            val resultTargetAchievement = async {
                                                dashboardViewModel.getTargetAchievementData(
                                                    "bearer ${appSessionManager.getSessionToken()}",
                                                    campaign.id.toString()
                                                )
                                            }

                                            awaitAll(resultSurveyData, resultTargetAchievement)

                                            //back to the main thread
                                            withContext(Dispatchers.Main) {
                                                navController.navigate(Screen.DynamicScreen.route)
                                            }
                                        }

                                    }
                                    .padding(start = 16.dp, end = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = campaign.name,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    fontWeight = Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            when (val state = extraServiceDataState.value){
                is UIState.Error -> {
                    Toasty.error(context, state.exception.message.toString(), Toasty.LENGTH_SHORT).show()
                }
                is UIState.Loading -> {
                }
                is UIState.Success -> {
                    val userData = gson.toJson(state.data)
                    appSessionManager.setExtraServiceCamInfo(userData)
                }
            }
        }
    }
}
