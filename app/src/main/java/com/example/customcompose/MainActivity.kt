package com.example.customcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.customcompose.model.JSON_STRING
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.ui.theme.CustomComposeTheme
import com.example.customcompose.views.DynamicScreen
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    val gson = Gson()
    val surveyDataModel = gson.fromJson(JSON_STRING, SurveyDataModel::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomComposeTheme {
                Surface (
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ){
                    DynamicScreen(surveyDataModel)
                }
            }
        }
    }
}