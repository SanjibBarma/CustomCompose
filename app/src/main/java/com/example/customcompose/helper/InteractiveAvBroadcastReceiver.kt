package com.example.customcompose.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.customcompose.MyApplication.Companion.blockListViewModel
import com.example.customcompose.helper.Constants.surveyBasicInfo


class InteractiveAvBroadcastReceiver() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val data = intent.getStringExtra("vr_data")
        val vr_start_tim = intent.getStringExtra("vr_start_time")
        val vr_end_tim = intent.getStringExtra("vr_end_time")
        val vr_activity_index = intent.getStringExtra("vr_activity_index")

        Log.d("InteractiveAvBroadcastReceiver", "Received Broadcast: Start Time: $data, End Time: $vr_start_tim, Status: $vr_end_tim")

        if (data == "True"){
            val vrMap: HashMap<String, Any> = hashMapOf()
            vrMap["interactive_av_status"] = data
            vrMap["interactive_av_start_time"] = vr_start_tim ?: ""
            vrMap["interactive_av_end_time"] = vr_end_tim ?: ""
            vrMap["interactive_av_activity_index"] = vr_activity_index ?: ""

            surveyBasicInfo["interactive_av"] = vrMap

            blockListViewModel.moveToNextFromAv()
        }
    }
}
