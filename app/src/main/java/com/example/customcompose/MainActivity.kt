package com.example.customcompose

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.customcompose.helper.AudioRecorderService
import com.example.customcompose.model.LOCATION_STRING
import com.example.customcompose.model.RoutePlanData
import com.example.customcompose.navigation.Navigation
import com.example.customcompose.ui.theme.CustomComposeTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    private val gson = Gson()
    private val routePlanList: List<RoutePlanData> = gson.fromJson(LOCATION_STRING, Array<RoutePlanData>::class.java).toList()

    private val requiredPermissions = mutableListOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.CAMERA
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (!allGranted) {
                Toast.makeText(this, "All permissions are required for full functionality", Toast.LENGTH_LONG).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!hasRequiredPermissions()) {
            requestPermissionLauncher.launch(requiredPermissions)
        }

        val numberValidationViewModel = MyApplication.surveyFlowViewModel
        val loginViewModel = MyApplication.loginViewModel
        val blockListViewModel = MyApplication.blockListViewModel

        setContent {
            CustomComposeTheme {
//                DynamicScreen(blockListViewModel, surveyDataModelList, routePlanList, numberValidationViewModel)
//                LoginScreen(loginViewModel)
                Navigation(
                    blockListViewModel,
                    numberValidationViewModel,
                    loginViewModel,
                    routePlanList
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAudioService()
    }

    private fun stopAudioService() {
        val intent = Intent(this, AudioRecorderService::class.java)
        stopService(intent)
    }

    private fun hasRequiredPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}