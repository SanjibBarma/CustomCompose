package com.example.customcompose.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.customcompose.helper.SharedPrefHelper
import com.example.customcompose.model.RoutePlanListData
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.model.TARGET_ACHIEVEMENT_LIST
import com.example.customcompose.model.TargetAchievement
import com.example.customcompose.ui.theme.RouteHeading
import com.example.customcompose.viewmodel.BlockListViewModel
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

@Composable
fun RoutePlanView(
    blockListViewModel: BlockListViewModel,
    surveyDataModelList: List<SurveyDataModel>,
    onDismiss: () -> Unit
) {

    val routeListItem by blockListViewModel.routeListItem.collectAsState()
    val listState = remember { LazyListState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val sharedPrefHelper = remember { SharedPrefHelper(context) }
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .background(Color.White)
        ) {
            LaunchedEffect(routeListItem.size) {
                if (routeListItem.isNotEmpty()) {
                    coroutineScope.launch {
                        listState.scrollToItem(routeListItem.size - 1)
                    }
                }
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.imePadding().fillMaxSize()
            ) {
                items(routeListItem) { routeItem ->
                    Spacer(modifier = Modifier.height(32.dp))
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(RouteHeading)
                    ){
                        Text(
                            routeItem.typeTitle,
                            modifier = Modifier
                                .padding(12.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        println("ListPosition: ${routeItem.typeTitle} and ${routeItem.listPosition}")
                    }
                    RouteChildView(routeItem, blockListViewModel, surveyDataModelList, routeItem.listPosition)
                }
            }


        }
    }
}


@Composable
fun RouteChildView(
    locations: RoutePlanListData,
    blockListViewModel: BlockListViewModel,
    surveyDataModelList: List<SurveyDataModel>,
    listPosition: Int
) {
    val context = LocalContext.current
    val sharedPrefHelper = remember { SharedPrefHelper(context) }
    val selectedItemId = remember { mutableStateOf<Int?>(null) }

    val gson = Gson()
    val targetAchievementList: List<TargetAchievement> = gson.fromJson(TARGET_ACHIEVEMENT_LIST, Array<TargetAchievement>::class.java).toList()

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
                                selectedItemId.value = option.id

                                //location list er first position check kora hocche
                                //means route list theke selected id ber kora hobe
                                if (listPosition == 0){
                                    for (targetAchieve in targetAchievementList){
                                        if (targetAchieve.products.isNullOrEmpty() && targetAchieve.products.size == 0){
                                            for (locationTarget in targetAchieve.locations){
                                                //selected loc id and target locations er jekono id jodi match kore tahole
                                                // daily target and achievement compare kore warning dekhabe and return korbe
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

                                val routePlaneHistory = SurveyHistoryModel(
                                    question = option.type_slug,
                                    answer = option.name,
                                    id = option.id.toString()
                                )
                                option.surveyHistoryModel = listOf(routePlaneHistory)

                                if (!option.locations.isNullOrEmpty()) {
                                    blockListViewModel.addNextRoutePlanData(option.locations[0].type_slug, option.locations, listPosition+1)
                                } else {
                                    blockListViewModel.hideRoutePlanView()
                                    sharedPrefHelper.savePreviousGroupId("")
                                    sharedPrefHelper.clearCheckList()
                                    blockListViewModel.addBlockToTheSurveyFlow(surveyDataModelList[0].blocks[0].id!!, surveyDataModelList[0].group)
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
