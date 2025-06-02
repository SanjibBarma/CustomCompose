package com.example.customcompose.views.compose.helper_compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShowProgress(progress: Float, title: String, fileCount: Int, fileSize: Int) {
    Spacer(modifier = Modifier.height(32.dp))
    Row (
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(60.dp)
        ) {
            CircularProgressIndicator(
                progress = 1f,
                color = Color.LightGray,
                strokeWidth = 8.dp,
                modifier = Modifier.fillMaxSize()
            )
            CircularProgressIndicator(
                progress = progress / 100f,
                color = Color.Blue,
                strokeWidth = 8.dp,
                modifier = Modifier.fillMaxSize()
            )
            if (progress == 100f) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Download Complete",
                    tint = Color.Blue,
                    modifier = Modifier.size(40.dp)
                )
            } else {
                Text(
                    text = "${progress.toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title)
            Text("$fileCount of $fileSize", fontSize = 12.sp)
        }
    }
}
