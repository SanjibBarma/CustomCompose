package com.example.customcompose.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customcompose.helper.ConnectivityObserver
import com.example.customcompose.helper.UIState
import com.example.customcompose.model.number_validation.GiveAbleAchievement
import com.example.customcompose.model.number_validation.NumberCheckModel
import com.example.customcompose.repository.NumberValidationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NumberValidationViewModel(
    private val numberValidationRepository: NumberValidationRepository,
    private val connectivityObserver: ConnectivityObserver
): ViewModel() {

    private val _checkNumberData = MutableLiveData<UIState<NumberCheckModel>>(UIState.Loading)
    val checkNumberData: LiveData<UIState<NumberCheckModel>> = _checkNumberData

    private val _achievementData = MutableLiveData<UIState<GiveAbleAchievement>>(UIState.Loading)
    val achievementData: LiveData<UIState<GiveAbleAchievement>> = _achievementData

    fun checkNumber(token: String, requestBody: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            if (connectivityObserver.checkInternetConnection()) {
                _checkNumberData.postValue(UIState.Loading)

                try {
                    val response = numberValidationRepository.checkNumber(token, requestBody)

                    if (response.isSuccessful) {
                        // API Call was successful
                        response.body()?.let { responseBody ->
                            _checkNumberData.postValue(UIState.Success(responseBody))
                        } ?: run {
                            _checkNumberData.postValue(UIState.Error(Exception("Empty response from server")))
                        }
                    } else {
                        // API Call was unsuccessful, handle error response
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        _checkNumberData.postValue(UIState.Error(Exception("Error ${response.code()}: $errorMessage")))
                    }

                } catch (e: Exception) {
                    _checkNumberData.postValue(UIState.Error(e))
                }
            } else {
                _checkNumberData.postValue(UIState.Error(Exception("No internet connection")))
            }
        }
    }

    fun getAchievementData(token: String, id: String/*, requestBody: HashMap<String, Any>*/) {
        viewModelScope.launch(Dispatchers.IO) {
            if (connectivityObserver.checkInternetConnection()) {
                _achievementData.postValue(UIState.Loading)

                try {
                    val response = numberValidationRepository.getAchievementData(token, id)

                    if (response.isSuccessful) {
                        // API Call was successful
                        response.body()?.let { responseBody ->
                            _achievementData.postValue(UIState.Success(responseBody))
//                            checkNumber(token, requestBody)
                        } ?: run {
                            _achievementData.postValue(UIState.Error(Exception("Empty response from server")))
                        }
                    } else {
                        // API Call was unsuccessful, handle error response
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        _achievementData.postValue(UIState.Error(Exception("Error ${response.code()}: $errorMessage")))
                    }

                } catch (e: Exception) {
                    _achievementData.postValue(UIState.Error(e))
                }
            } else {
                _achievementData.postValue(UIState.Error(Exception("No internet connection")))
            }
        }
    }

}