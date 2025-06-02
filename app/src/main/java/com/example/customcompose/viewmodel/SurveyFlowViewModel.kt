package com.example.customcompose.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.MyApplication.Companion.connectivityObserver
import com.example.customcompose.storage.entity.PtrProgressEntity
import com.example.customcompose.helper.UIState
import com.example.customcompose.model.GiveAbleAchievement
import com.example.customcompose.model.NumberCheckModel
import com.example.customcompose.repository.SurveyFlowRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SurveyFlowViewModel(
    private val surveyFlowRepository: SurveyFlowRepository
): ViewModel() {
    private val _checkNumberData = MutableLiveData<UIState<NumberCheckModel>>(UIState.Loading)
    val checkNumberData: LiveData<UIState<NumberCheckModel>> = _checkNumberData
    val gson = Gson()

    //check number validation api
    fun checkNumber(token: String, requestBody: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            if (connectivityObserver.checkInternetConnection()) {
                _checkNumberData.postValue(UIState.Loading)

                try {
                    val response = surveyFlowRepository.checkNumber(token, requestBody)

                    if (response.isSuccessful) {
                        // API Call was successful
                        response.body()?.let { responseBody ->
                            _checkNumberData.postValue(UIState.Success(responseBody))

                            appSessionManager.setMobileVerificationData(gson.toJson(responseBody.data[0]))
                        } ?: run {
                            _checkNumberData.postValue(UIState.Error(Exception("Empty response from server")))
                        }
                    } else {
                        val errorResponse = response.errorBody()?.let { errorBody ->
                            val errorMessage = errorBody.string()
                            val apiError = Gson().fromJson(errorMessage, NumberCheckModel::class.java)
                            apiError.message ?: "Unknown error"
                        } ?: "Unknown error"
                        _checkNumberData.postValue(UIState.Error(Exception("Error ${response.code()}: $errorResponse")))
                    }

                } catch (e: Exception) {
                    _checkNumberData.postValue(UIState.Error(e))
                }
            } else {
                _checkNumberData.postValue(UIState.Error(Exception("No internet connection")))
            }
        }
    }

    //get givable list api
    private val _achievementData = MutableLiveData<UIState<GiveAbleAchievement>>(UIState.Loading)
    val achievementData: LiveData<UIState<GiveAbleAchievement>> = _achievementData

    fun getAchievementData(token: String, id: String, requestBody: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            if (connectivityObserver.checkInternetConnection()) {
                _achievementData.postValue(UIState.Loading)

                try {
                    val response = surveyFlowRepository.getAchievementData(token, id)

                    if (response.isSuccessful) {
                        // API Call was successful
                        response.body()?.let { responseBody ->
                            _achievementData.postValue(UIState.Success(responseBody))
                            checkNumber(token, requestBody)

                            val ptrData = appSessionManager.getCampaignId()?.let {
                                PtrProgressEntity(
                                    campId = it,
                                    brId = appSessionManager.getBrId()!!,
                                    ptrData = gson.toJson(responseBody)
                                )
                            }

                            if (ptrData != null) {
                                surveyFlowRepository.upsertPtrData(ptrData)
                            }

                        } ?: run {
                            _achievementData.postValue(UIState.Error(Exception("Empty response from server")))
                        }
                    } else {
                        val errorResponse = response.errorBody()?.let { errorBody ->
                            val errorMessage = errorBody.string()
                            val apiError = Gson().fromJson(errorMessage, GiveAbleAchievement::class.java)
                            apiError.message ?: "Unknown error"
                        } ?: "Unknown error"
                        _achievementData.postValue(UIState.Error(Exception("Error ${response.code()}: $errorResponse")))
                    }

                } catch (e: Exception) {
                    _achievementData.postValue(UIState.Error(e))
                }
            } else {
                _achievementData.postValue(UIState.Error(Exception("No internet connection")))
            }
        }
    }

    //reset number validation
    fun resetNumberValidationState(){
        _checkNumberData.value = UIState.Loading
        _achievementData.value = UIState.Loading
    }


    //send otp
    private val _otpData = MutableLiveData<UIState<JsonObject>>(UIState.Loading)
    val otpData: LiveData<UIState<JsonObject>> = _otpData

    fun sendOtp(token: String, requestBody: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            if (connectivityObserver.checkInternetConnection()) {
                _otpData.postValue(UIState.Loading)

                try {
                    val response = surveyFlowRepository.sendOtp( token, requestBody)

                    if (response.isSuccessful) {
                        // API Call was successful
                        response.body()?.let { responseBody ->
                            _otpData.postValue(UIState.Success(responseBody))

                        } ?: run {
                            _otpData.postValue(UIState.Error(Exception("Empty response from server")))
                        }
                    } else {
                        val errorResponse = response.errorBody()?.let { errorBody ->
                            val errorMessage = errorBody.string()
                            errorMessage ?: "Unknown error"
                        } ?: "Unknown error"
                        _otpData.postValue(UIState.Error(Exception("Error ${response.code()}: $errorResponse")))
                    }

                } catch (e: Exception) {
                    _otpData.postValue(UIState.Error(e))
                }
            } else {
                _otpData.postValue(UIState.Error(Exception("No internet connection")))
            }
        }
    }








    //======================************************============================//
    //*********************** get local data **********************************//
    //========================************************=========================//

    private val _prtData = MutableLiveData<PtrProgressEntity?>()
    val prtData: LiveData<PtrProgressEntity?> = _prtData

    fun fetchPtrDataById(brId: String, campId: String) {
        viewModelScope.launch (Dispatchers.IO){
            surveyFlowRepository.getPtrData(brId, campId)
//                .flowOn(Dispatchers.IO)
                .collect { result ->
                    _prtData.postValue(result)  // Post value to LiveData (safe for background threads)
                    println("ptrDataState_repo: $result")
                }
        }
    }
}