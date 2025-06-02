package com.example.customcompose.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.customcompose.MyApplication.Companion.blockListViewModel
import com.example.customcompose.model.SurveyHistoryModel

class GameBroadcastReceiver() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val startTime = intent.getStringExtra("start_time")
        val endTime = intent.getStringExtra("end_time")
        val gameStatus = intent.getBooleanExtra("status", false)

        Log.d("GameBroadcastReceiver", "Received Broadcast: Start Time: $startTime, End Time: $endTime, Status: $gameStatus")

        if (gameStatus){
            blockListViewModel.moveToNextFromGame()
        }
    }
}
