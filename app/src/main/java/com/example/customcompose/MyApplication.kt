package com.example.customcompose

import android.app.Application
import com.example.customcompose.storage.AppDatabase
import com.example.customcompose.storage.AppSessionManager
import com.example.customcompose.helper.ConnectivityObserver
import com.example.customcompose.network.ApiService
import com.example.customcompose.network.RetrofitInstance
import com.example.customcompose.repository.DashboardRepository
import com.example.customcompose.repository.LoginRepository
import com.example.customcompose.repository.SurveyFlowRepository
import com.example.customcompose.viewmodel.BlockListViewModel
import com.example.customcompose.viewmodel.DashboardViewModel
import com.example.customcompose.viewmodel.LoginViewModel
import com.example.customcompose.viewmodel.SurveyFlowViewModel

class MyApplication : Application() {

    companion object {
        val appSessionManager: AppSessionManager by lazy {
            instance?.let { AppSessionManager(it) }
                ?: throw IllegalStateException("Application instance is not initialized")
        }

        val apiService: ApiService by lazy {
            RetrofitInstance.apiService
        }

        val mediaService: ApiService by lazy {
            RetrofitInstance.mediaService
        }

        private val appDatabase: AppDatabase by lazy {
            instance?.let { AppDatabase.getDatabase(it) }
                ?: throw IllegalStateException("Application instance is not initialized")
        }

        val connectivityObserver: ConnectivityObserver by lazy {
            instance?.let { ConnectivityObserver(it) }
                ?: throw IllegalStateException("Application instance is not initialized")
        }

        private val loginRepository: LoginRepository by lazy {
            LoginRepository(appDatabase.dbDao())
        }

        val dashboardRepository: DashboardRepository by lazy {
            DashboardRepository(appDatabase.dbDao())
        }

        val loginViewModel: LoginViewModel by lazy {
            LoginViewModel(loginRepository)
        }

        val dashboardViewModel: DashboardViewModel by lazy {
            DashboardViewModel(dashboardRepository)
        }

        val blockListViewModel: BlockListViewModel by lazy {
            instance?.let { BlockListViewModel(it) }
                ?: throw IllegalStateException("Application instance is not initialized")
        }

        private val surveyFlowRepository: SurveyFlowRepository by lazy {
            SurveyFlowRepository(appDatabase.dbDao())
        }

        val surveyFlowViewModel: SurveyFlowViewModel by lazy {
            SurveyFlowViewModel(surveyFlowRepository)
        }

        private var instance: MyApplication? = null

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}

