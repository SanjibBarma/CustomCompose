package com.example.customcompose.views.compose.referring

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun LocationBlock(blockListViewModel: BlockListViewModel, isActiveGroup: Boolean) {
    val routeListItem by blockListViewModel.routeParentList.collectAsState()

    val sourceLocationName = remember { mutableStateOf("") }
    val targetLocationName = remember { mutableStateOf("") }

    val sourceLocation = routeListItem[0].selectedId
    val locationId = routeListItem[routeListItem.size - 1].selectedId

    for (parentLocation in routeListItem){
        for (targetLocation in parentLocation.locationList!!){
            if (sourceLocation == targetLocation.id){
                println("sourceLocationName: ${targetLocation.name}")
                sourceLocationName.value = targetLocation.name
            }

            if (locationId == targetLocation.id){
                println("LocationName: ${targetLocation.name}")
                targetLocationName.value = targetLocation.name
            }
        }
    }

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
                "Location: ${sourceLocationName.value}, ${targetLocationName.value}",
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}