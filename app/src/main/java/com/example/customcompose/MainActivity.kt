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
import com.example.customcompose.app_database.AppDatabase
import com.example.customcompose.helper.AudioRecorderService
import com.example.customcompose.helper.ConnectivityObserver
import com.example.customcompose.model.LOCATION_STRING
import com.example.customcompose.model.RoutePlanData
import com.example.customcompose.navigation.Navigation
import com.example.customcompose.network.RetrofitInstance
import com.example.customcompose.repository.LoginRepository
import com.example.customcompose.repository.NumberValidationRepository
import com.example.customcompose.ui.theme.CustomComposeTheme
import com.example.customcompose.viewmodel.BlockListViewModel
import com.example.customcompose.viewmodel.LoginViewModel
import com.example.customcompose.viewmodel.SurveyFlowViewModel
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    private val gson = Gson()
//    private val surveyDataModelList: List<SurveyDataModel> = gson.fromJson(SURVEY_FLOW_JSON, Array<SurveyDataModel>::class.java).toList()
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

        val apiService = RetrofitInstance.apiService
        val numberValidationRepository = NumberValidationRepository(apiService)
        val connectivityObserver = ConnectivityObserver(applicationContext)
        val numberValidationViewModel = SurveyFlowViewModel(numberValidationRepository, connectivityObserver)

        if (!hasRequiredPermissions()) {
            requestPermissionLauncher.launch(requiredPermissions)
        }
        val appDatabase = AppDatabase.getDatabase(applicationContext)
        val loginRepository = LoginRepository(apiService, appDatabase.dbDao())
        val loginViewModel = LoginViewModel(loginRepository, connectivityObserver)


        //surveyFlow
        val blockListViewModel = BlockListViewModel(applicationContext)

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