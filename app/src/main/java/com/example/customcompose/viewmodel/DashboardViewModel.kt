package com.example.customcompose.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.MyApplication.Companion.connectivityObserver
import com.example.customcompose.app_database.entity.SurveyDataEntity
import com.example.customcompose.app_database.entity.TargetAchievementEntity
import com.example.customcompose.helper.UIState
import com.example.customcompose.model.AchievementData
import com.example.customcompose.model.ExtraServiceModel
import com.example.customcompose.model.number_validation.NumberCheckModel
import com.example.customcompose.repository.DashboardRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class DashboardViewModel(private val dashboardRepository: DashboardRepository) : ViewModel(){

    private val _extraServiceData = MutableLiveData<UIState<ExtraServiceModel>>(UIState.Loading)
    val extraServiceData: LiveData<UIState<ExtraServiceModel>> = _extraServiceData

    private val _isExtraService = MutableStateFlow(false)
    val isExtraService = _isExtraService.asStateFlow()

    val gson = Gson()

    //check number validation api
    fun getExtraServices(token: String, requestBody: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            if (connectivityObserver.checkInternetConnection()) {
                _extraServiceData.postValue(UIState.Loading)

                try {
                    val response = dashboardRepository.getExtraServiceData(token, requestBody)

                    if (response.isSuccessful) {
                        // API Call was successful
                        response.body()?.let { responseBody ->
                            _extraServiceData.postValue(UIState.Success(responseBody))
                        } ?: run {
                            _extraServiceData.postValue(UIState.Error(Exception("Empty response from server")))
                        }
                    } else {
                        val errorResponse = response.errorBody()?.let { errorBody ->
                            val errorMessage = errorBody.string()
                            val apiError = Gson().fromJson(errorMessage, NumberCheckModel::class.java)
                            apiError.message ?: "Unknown error"
                        } ?: "Unknown error"
                        _extraServiceData.postValue(UIState.Error(Exception("Error ${response.code()}: $errorResponse")))
                    }

                } catch (e: Exception) {
                    _extraServiceData.postValue(UIState.Error(e))
                }
            } else {
                _extraServiceData.postValue(UIState.Error(Exception("No internet connection")))
            }
        }
    }


    //==============================*************=============================//
    //get target achievement info
    private val _targetAchievementData = MutableLiveData<UIState<AchievementData>>(UIState.Loading)
    val targetAchievementData: LiveData<UIState<AchievementData>> = _targetAchievementData

    fun getTargetAchievementData(token: String, cmpId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (connectivityObserver.checkInternetConnection()) {
                _targetAchievementData.postValue(UIState.Loading)

                try {
                    val response = dashboardRepository.getAchievementInfo(token, cmpId)

                    if (response.isSuccessful) {
                        // API Call was successful
                        response.body()?.let { responseBody ->
                            _targetAchievementData.postValue(UIState.Success(responseBody))

                            if (responseBody.data != null){
                                for (targetAchievementData in responseBody.data.targetAchievements){
                                    val targetAchievement = TargetAchievementEntity(
                                        target_achievement = gson.toJson( responseBody.data.targetAchievements),
                                        target_id = targetAchievementData.id,
                                        user_id = appSessionManager.getBrId()!!,
                                        campaign_id = appSessionManager.getCampaignId()!!
                                    )

                                    dashboardRepository.upsertTargetAchievementData(targetAchievement)
                                }
                            }

                        } ?: run {
                            _targetAchievementData.postValue(UIState.Error(Exception("Empty response from server")))
                        }
                    } else {
                        val errorResponse = response.errorBody()?.let { errorBody ->
                            val errorMessage = errorBody.string()
                            val apiError = Gson().fromJson(errorMessage, NumberCheckModel::class.java)
                            apiError.message ?: "Unknown error"
                        } ?: "Unknown error"
                        _targetAchievementData.postValue(UIState.Error(Exception("Error ${response.code()}: $errorResponse")))
                    }

                } catch (e: Exception) {
                    _targetAchievementData.postValue(UIState.Error(e))
                }
            } else {
                _targetAchievementData.postValue(UIState.Error(Exception("No internet connection")))
            }
        }
    }




    //======================************************============================//
    //*********************** get local data **********************************//
    //========================************************=========================//

    private val _localTargetAchieveData = MutableLiveData<TargetAchievementEntity?>()
    val localTargetAchieveData: LiveData<TargetAchievementEntity?> = _localTargetAchieveData

    fun fetchTargetAchieveDataByIds(brId: String, campId: String) {
        viewModelScope.launch {
            dashboardRepository.getTargetAchievementData(brId, campId)
                .flowOn(Dispatchers.IO)  // Ensure it's on IO thread
                .collect { result ->
                    _localTargetAchieveData.postValue(result)
                    println("targetAchieveData_repo: $result")
                }
        }
    }
}