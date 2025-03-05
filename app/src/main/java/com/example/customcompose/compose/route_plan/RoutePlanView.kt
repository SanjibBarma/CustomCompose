package com.example.customcompose.compose.route_plan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.ui.theme.RouteHeading
import com.example.customcompose.viewmodel.BlockListViewModel
import kotlinx.coroutines.launch

@Composable
fun RoutePlanView(
    blockListViewModel: BlockListViewModel,
    surveyDataModelList: List<SurveyDataModel>,
    onDismiss: () -> Unit
) {

    val routeListItem by blockListViewModel.routeParentList.collectAsState()
    val listState = remember { LazyListState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
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

