package com.example.customcompose

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.customcompose.model.JSON_STRING
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.ui.theme.CustomComposeTheme
import com.example.customcompose.views.DynamicScreen
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    val gson = Gson()
    val surveyDataModelList: List<SurveyDataModel> = gson.fromJson(JSON_STRING, Array<SurveyDataModel>::class.java).toList()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, proceed with camera functionality
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }

        setContent {
            CustomComposeTheme {
                Surface (
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ){
                    DynamicScreen(surveyDataModelList)
                }
            }
        }
    }
}