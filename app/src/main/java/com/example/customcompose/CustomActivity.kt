package com.example.customcompose

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.example.customcompose.views.compose.referring.video.FullScreenVideoPopup

class CustomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val videoUriString = intent.getStringExtra("video_uri")
        val destination = intent.getStringExtra("destination")
        val videoUri = videoUriString?.let { Uri.parse(it) }
        val blockId = intent.getStringExtra("blockId")
        val groupId = intent.getStringExtra("groupId")
        val position = intent.getStringExtra("position")

        println("video_receiver: blockid: $blockId   groupId: $groupId   position: $position")

        setContent {
            videoUri?.let {
                FullScreenVideoPopup(
                    it,
                    blockId,
                    groupId,
                    destination,
                    position)
            } ?: Text("Invalid video URI")
        }

    }
}