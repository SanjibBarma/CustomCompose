package com.example.customcompose.views.compose.referring

import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.customcompose.MyApplication.Companion.blockListViewModel
import com.example.customcompose.R
import com.example.customcompose.helper.GameBroadcastReceiver
import com.example.customcompose.helper.InteractiveAvBroadcastReceiver
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel

@Composable
fun InteractiveAvBlock (
    block: Block,
    isActiveGroup: Boolean,
    destination: String
){
    val currentBlockId = block.id ?: ""
    val isSkippable = block.skip?.id != "-1"

    val question = block.question?.alias ?: ""
    val context = LocalContext.current
    val packageName = block.options?.get(0)?.value
//    val packageName = "com.batb.bhn_tog"
    val isAvBroadCast by blockListViewModel.isShowAv.collectAsState()

    DisposableEffect(context) {
        val filter = IntentFilter("com.example.batcampaign")
        val receiver = InteractiveAvBroadcastReceiver()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        }

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    LaunchedEffect(isAvBroadCast) {
        println("isAvBroadCast $isAvBroadCast")

        if (isAvBroadCast) {
            val historyModel = SurveyHistoryModel(
                question = question,
                answer = "Yes",
                id = currentBlockId
            )


            block.surveyHistoryModel = listOf(historyModel)
            block.referTo?.group_no?.let { groupId ->
                block.referTo.id?.let { nextBlockId ->
                    if (destination == "mainSurvey"){
                        blockListViewModel.addBlockToTheSurveyFlow(nextBlockId, groupId, block.position)
                    }else{
                        blockListViewModel.addBlockToTheCheckList(nextBlockId, groupId)
                    }
                }
            }
            blockListViewModel.stayInAv()
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
            modifier = Modifier.padding(8.dp)
        ) {
            Text(block.question!!.slug)
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .clickable(enabled = isActiveGroup) {
                        try {
                            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName ?: "")
                            if (launchIntent != null) {
                                context.startActivity(launchIntent)
                            } else {
                                Toast.makeText(context, "Package not found", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Unable to open app", Toast.LENGTH_SHORT).show()
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_play_av),
                    contentDescription = "Click image",
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                )
            }

            if (isSkippable) {
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val surveyHistoryModel = SurveyHistoryModel(
                            question = "",
                            answer = "",
                            id = currentBlockId
                        )
                        block.surveyHistoryModel = listOf(surveyHistoryModel)

                        block.skip?.id?.let { blockId ->
                            block.skip.group_no.let { groupId ->
                                if (destination == "mainSurvey") {
                                    blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId, block.position)
                                } else {
                                    blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isActiveGroup
                ) {
                    Text("Skip")
                }
            }

        }
    }
}