package com.example.customcompose.compose

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.customcompose.model.Block
import com.google.gson.Gson

@Composable
fun SubmitButton(inputData: MutableMap<String, String>, flatten: List<Block>) {
    Button(
        onClick = {
            val gson = Gson()
            val json = gson.toJson(inputData)
            Log.d("SURVEY_DATA", json)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Submit")
    }
}