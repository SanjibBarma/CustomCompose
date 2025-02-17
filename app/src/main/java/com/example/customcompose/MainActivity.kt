package com.example.customcompose

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.customcompose.lazy_column_test.TestScreen
import com.example.customcompose.model.JSON_STRING
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.ui.theme.CustomComposeTheme
import com.example.customcompose.lazy_column_test.ListViewModel
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    val gson = Gson()
    val surveyDataModelList: List<SurveyDataModel> = gson.fromJson(JSON_STRING, Array<SurveyDataModel>::class.java).toList()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
            } else {
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
//                DynamicScreenTest(surveyDataModelList)
//                DynamicScreen(surveyDataModelList)
                val viewModel = ListViewModel()
                TestScreen(viewModel)
            }
        }
    }
}