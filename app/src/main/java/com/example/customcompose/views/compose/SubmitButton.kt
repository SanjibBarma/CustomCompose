package com.example.customcompose.views.compose

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.MyApplication.Companion.connectivityObserver
import com.example.customcompose.MyApplication.Companion.dashboardRepository
import com.example.customcompose.storage.entity.TargetAchievementEntity
import com.example.customcompose.helper.CommonUtils.getDeviceInfo
import com.example.customcompose.helper.CommonUtils.getdatetime
import com.example.customcompose.helper.Constants.fullCampaignData
import com.example.customcompose.helper.Constants.surveyBasicInfo
import com.example.customcompose.helper.SntpClient
import com.example.customcompose.model.TargetAchievement
import com.example.customcompose.viewmodel.BlockListViewModel
import com.example.customcompose.viewmodel.InvisibleHistoryData.invisibleHistoryList
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SubmitButton(blockListViewModel: BlockListViewModel) {
    val context = LocalContext.current
    var dhakaTime by remember { mutableStateOf<ZonedDateTime?>(null) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val gson = GsonBuilder().setPrettyPrinting().create()
    val surveyDataMap = mutableMapOf<String, HashMap<String, String>>()

    Button(
        onClick = {
            CoroutineScope(Dispatchers.IO).launch {

                val achievement: TargetAchievement? = appSessionManager.getCurrentTargetAchievement()

                if (achievement != null) {
                    achievement.daily_achievement +=1

                    val targetAchievement = TargetAchievementEntity(
                        target_achievement = gson.toJson(achievement),
                        target_id = achievement.id,
                        user_id = appSessionManager.getBrId()!!,
                        campaign_id = appSessionManager.getCampaignId()!!
                    )

                    dashboardRepository.upsertTargetAchievementData(targetAchievement)
                }

                for (surveyBlockHistory in blockListViewModel.parentSurveyBlockList.value) {
                    if (surveyBlockHistory?.surveyHistoryModel != null) {
                        for (surveyHistory in surveyBlockHistory.surveyHistoryModel) {
                            if (surveyHistory != null) {
                                surveyDataMap[surveyHistory.id] = hashMapOf(
                                    "answer" to surveyHistory.answer,
                                    "question" to surveyHistory.question
                                )
                            }
                        }
                    }
                }

                if (invisibleHistoryList.isNotEmpty()) {
                    for (invHistory in invisibleHistoryList) {
                        surveyDataMap[invHistory.id] = hashMapOf(
                            "answer" to invHistory.answer,
                            "question" to invHistory.question
                        )
                    }
                }

                surveyBasicInfo["device_info"] = getDeviceInfo(context)
                surveyBasicInfo["survey_data"] = surveyDataMap
                surveyBasicInfo["online_status"] = 1
                appSessionManager.getUsername()?.let { surveyBasicInfo["contacted_br"] = it }
                fullCampaignData?.let {
                    surveyBasicInfo["campaign_name"] = it.name
                    surveyBasicInfo["campaign_id"] = it.id.toString()
                    surveyBasicInfo["campaign_version"] = it.versions.campaign
                    surveyBasicInfo["video_version"] = it.versions.video
                    surveyBasicInfo["image_version"] = it.versions.image
                    surveyBasicInfo["audio_needed"] = it.conditions.audio
                    surveyBasicInfo["audio_recorded"] = it.conditions.audio
                }
                if (connectivityObserver.checkInternetConnection()) {
                    val utcMillis = SntpClient.getUtcTime()
                    utcMillis?.let {
                        val instant = Instant.ofEpochMilli(it)
                        val zoneId = ZoneId.of("Asia/Dhaka")
                        val zonedTime = ZonedDateTime.ofInstant(instant, zoneId)
                        dhakaTime = zonedTime
                    }
                    surveyBasicInfo["end"] = dhakaTime!!.format(formatter)
                } else {
                    surveyBasicInfo["end"] = getdatetime()
                }
                surveyBasicInfo["long"] = 90.3973018
                surveyBasicInfo["lat"] = 23.7828891
                surveyBasicInfo["radius"] = 11.482
//                surveyBasicInfo["tap_analysis"] = tapAnalysisList


                val comboJson = gson.toJson(surveyBasicInfo)
                Log.d("SURVEY_COMBO_DATA", comboJson)
                println("SURVEY_COMBO_DATA: $comboJson")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Submit")
    }
}

val tapAnalysisList = listOf(
    mapOf(
        "block_id" to "11",
        "question" to "otp",
        "result" to listOf(
            mapOf("option" to "next_button", "tap_time" to "50154")
        ),
        "survey_start_time" to "2025-05-07 15:06:24"
    ),
    mapOf(
        "block_id" to "33",
        "question" to "option_selection",
        "result" to listOf(
            mapOf("option" to "Next", "tap_time" to "193795")
        ),
        "survey_start_time" to "2025-05-07 15:06:24"
    )
)
