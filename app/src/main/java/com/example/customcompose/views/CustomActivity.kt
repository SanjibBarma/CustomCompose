package com.example.customcompose.views

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import com.example.customcompose.model.Option
import com.example.customcompose.views.compose.referring.image_view.FullScreenImagePreview
import com.example.customcompose.views.compose.referring.video.FullScreenVideoPopup
import kotlinx.serialization.json.Json

class CustomActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val videoUriString = intent.getStringExtra("video_uri")
        val videoUri = videoUriString?.let { Uri.parse(it) }
        val fileType = intent.getStringExtra("fileView")

        val selectedIndex = intent.getStringExtra("selectedIndex")

        val json = intent.getStringExtra("options")
        val options: List<Option>? = json?.let {
            Json.decodeFromString<List<Option>>(it)
        }

        setContent {
            if (fileType.equals("video_view")){
                videoUri?.let {
                    FullScreenVideoPopup(it)
                } ?: Text("Invalid video URI")
            }else{
                if (options != null && selectedIndex != null) {
                    FullScreenImagePreview(
                        options = options,
                        selectedIndex = selectedIndex.toInt()
                    )
                }
            }
        }
    }
}