package com.example.customcompose.compose.route_plan

import android.util.Log
import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.MyApplication.Companion.dashboardViewModel
import com.example.customcompose.model.RoutePlanParentModel
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.model.TargetAchievement
import com.example.customcompose.viewmodel.BlockListViewModel
import com.google.gson.Gson
import es.dmoral.toasty.Toasty

@Composable
fun RouteChildView(
    locations: RoutePlanParentModel,
    blockListViewModel: BlockListViewModel,
    surveyDataModelList: List<SurveyDataModel>,
    listPosition: Int,
    onDismiss: () -> Unit
) {

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    onBackPressedDispatcher?.addCallback {
        //navController.popBackStack()
        onDismiss()
        Log.d("MyScreen", "Back button pressed on MyScreen")
    }

    val context = LocalContext.current
    val selectedItemId = remember { mutableStateOf(locations.selectedId ?: 0) }
    val routeListItem by blockListViewModel.routeParentList.collectAsState()

//    val targetAchievementList: List<TargetAchievement> = gson.fromJson(TARGET_ACHIEVEMENT_LIST, Array<TargetAchievement>::class.java).toList()
    appSessionManager.getBrId()?.let {
        dashboardViewModel.fetchTargetAchieveDataByIds(it, appSessionManager.getCampaignId()!!)
    }
    val targetAchievementState by dashboardViewModel.localTargetAchieveData.observeAsState()
    val gson = Gson()
    var targetAchievementList by remember { mutableStateOf<List<TargetAchievement>?>(emptyList()) }

    LaunchedEffect(targetAchievementState) {
        targetAchievementState?.target_achievement?.let {
            try {
                targetAchievementList = gson.fromJson(it, Array<TargetAchievement>::class.java).toList()
            } catch (e: Exception) {
                targetAchievementList = emptyList()
            }
        }
        println("targetAchieveData_compose: $targetAchievementList")
    }

    Spacer(modifier = Modifier.height(8.dp))


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 0.dp, max = 1000.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(locations.locationList ?: emptyList()) { option ->
                    val isSelected = selectedItemId.value == option.id

                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .size(56.dp)
                            .border(1.dp, if (isSelected) Color.Gray else Color.LightGray, RoundedCornerShape(4.dp))
                            .background(if (isSelected) Color.LightGray else Color.White)
                            .clickable {
                                locations.selectedId = option.id
                                selectedItemId.value = option.id

                                //location list er first position check kora hocche
                                //means route list theke selected id ber kora hobe
                                if (listPosition == 0){
                                    for (targetAchieve in targetAchievementList!!){
                                        if (targetAchieve.products.isNullOrEmpty() && targetAchieve.products.size == 0){
                                            for (locationTarget in targetAchieve.locations){
                                                //selected loc id and target locations er jekono id jodi match kore tahole
                                                // daily target and achievement compare kore daily_achievement over hoye gele warning dekhabe and return korbe
                                                if (option.id == locationTarget.id){
                                                    if (targetAchieve.daily_achievement >= targetAchieve.daily_target && !targetAchieve.over_achivement){
                                                        Toasty.warning(context, "No more target for this location", Toasty.LENGTH_SHORT).show()
                                                        return@clickable
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                println("selectedItemId: ${selectedItemId.value}")


                                if (!option.locations.isNullOrEmpty()) {
                                    blockListViewModel.addNextRoutePlanData(option.locations[0].type_slug, option.locations, listPosition+1)
                                } else {
                                    println("routelist_size: ${routeListItem.size}")
                                    blockListViewModel.hideRoutePlanView()
                                    appSessionManager.savePreviousGroupId("")
                                    appSessionManager.clearCheckList()
                                    blockListViewModel.addBlockToTheSurveyFlow(surveyDataModelList[0].blocks[0].id!!, surveyDataModelList[0].group, surveyDataModelList[0].blocks[0].position)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!option.name.isNullOrEmpty()) {
                            Text(
                                text = option.name,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}