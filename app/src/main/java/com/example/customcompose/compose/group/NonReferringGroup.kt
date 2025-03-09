package com.example.customcompose.compose.group

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.customcompose.model.Block
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun NonReferringGroup(
    blockListViewModel: BlockListViewModel,
    currentBlock: Block,
    position: Int?,
    isActiveGroup: Boolean,
    destination: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Non-Referring: ")
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = "", onValueChange = {  })
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    currentBlock.position?.let {position ->
                        blockListViewModel.addBlockToTheSurveyFlow(currentBlock.jumping_logic?.get(0)!!.id, currentBlock.jumping_logic[0].group_no, position)
                    }
                }
            ) {
                Text("Next")
            }
        }
    }
}
