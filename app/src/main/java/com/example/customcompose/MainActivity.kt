package com.example.customcompose

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.customcompose.model.JSON_STRING
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.ui.theme.CustomComposeTheme
import com.example.customcompose.viewmodel.BlockListViewModel
import com.example.customcompose.views.DynamicScreen
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    val gson = Gson()
    val surveyDataModelList: List<SurveyDataModel> = gson.fromJson(JSON_STRING, Array<SurveyDataModel>::class.java).toList()

    private val requiredPermissions = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.POST_NOTIFICATIONS,
        android.Manifest.permission.CAMERA
    )

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (!allGranted) {
                Toast.makeText(
                    this,
                    "All permissions are required for full functionality",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!hasRequiredPermissions()) {
            requestPermissionLauncher.launch(requiredPermissions)
        }



        val blockListViewModel = BlockListViewModel(applicationContext, surveyDataModelList)
        setContent {
            CustomComposeTheme {
                DynamicScreen(blockListViewModel, surveyDataModelList)
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}