package com.example.customcompose.compose.referring

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun LocationBlock(blockListViewModel: BlockListViewModel, isActiveGroup: Boolean) {
    val routeListItem by blockListViewModel.routeParentList.collectAsState()

//    val allAnswers = routeListItem
//        .flatMap { it.locationList.orEmpty() } // fetching all the data from locationList
//        .flatMap { it.routePlanHistory.orEmpty() } // now fetching all from surveyHistoryModel which is under locationList
//        .mapNotNull { it?.answer } // accepts null from surveyHistoryModel taking all the answer
//        .joinToString(", ") // seperating all the answer using ","

//    println("All_location: $allAnswers")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .background(if (isActiveGroup) Color.White else Color(0x80CCCCCC)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Location: ",/*$allAnswers*/
                modifier = Modifier
                    .padding(16.dp),
                fontSize = 14.sp
            )
        }
    }
}