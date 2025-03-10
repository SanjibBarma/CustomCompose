package com.example.customcompose

import android.app.Application
import com.example.customcompose.app_database.AppDatabase
import com.example.customcompose.helper.AppSessionManager
import com.example.customcompose.helper.ConnectivityObserver
import com.example.customcompose.network.ApiService
import com.example.customcompose.network.RetrofitInstance
import com.example.customcompose.repository.LoginRepository
import com.example.customcompose.repository.SurveyFlowRepository
import com.example.customcompose.viewmodel.BlockListViewModel
import com.example.customcompose.viewmodel.LoginViewModel
import com.example.customcompose.viewmodel.SurveyFlowViewModel

class MyApplication : Application() {

    companion object {
        val appSessionManager: AppSessionManager by lazy {
            instance?.let { AppSessionManager(it) }
                ?: throw IllegalStateException("Application instance is not initialized")
        }

        private val apiService: ApiService by lazy {
            RetrofitInstance.apiService
        }

        private val appDatabase: AppDatabase by lazy {
            instance?.let { AppDatabase.getDatabase(it) }
                ?: throw IllegalStateException("Application instance is not initialized")
        }

        private val connectivityObserver: ConnectivityObserver by lazy {
            instance?.let { ConnectivityObserver(it) }
                ?: throw IllegalStateException("Application instance is not initialized")
        }

        private val loginRepository: LoginRepository by lazy {
            LoginRepository(apiService, appDatabase.dbDao())
        }
        val loginViewModel: LoginViewModel by lazy {
            LoginViewModel(loginRepository, connectivityObserver)
        }

        val blockListViewModel: BlockListViewModel by lazy {
            instance?.let { BlockListViewModel(it) }
                ?: throw IllegalStateException("Application instance is not initialized")
        }

        private val surveyFlowRepository: SurveyFlowRepository by lazy {
            SurveyFlowRepository(apiService)
        }

        val surveyFlowViewModel: SurveyFlowViewModel by lazy {
            SurveyFlowViewModel(surveyFlowRepository, connectivityObserver)
        }

        private var instance: MyApplication? = null

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}

