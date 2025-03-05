package com.example.customcompose.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customcompose.app_database.entity.SignInEntity
import com.example.customcompose.helper.ConnectivityObserver
import com.example.customcompose.helper.UIState
import com.example.customcompose.model.SignInModel
import com.example.customcompose.model.number_validation.NumberCheckModel
import com.example.customcompose.repository.LoginRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val connectivityObserver: ConnectivityObserver
): ViewModel() {

    private val _loginData = MutableLiveData<UIState<SignInModel>>(UIState.Loading)
    val loginData: LiveData<UIState<SignInModel>> = _loginData

    fun getLoginInfo(signInMap: HashMap<String, Any>){
        viewModelScope.launch(Dispatchers.IO) {
            if (connectivityObserver.checkInternetConnection()) {
                _loginData.postValue(UIState.Loading)

                try {
                    val response = loginRepository.getLoginInfo(signInMap)

                    if (response.isSuccessful) {
                        // API Call was successful
                        response.body()?.let { responseBody ->
                            _loginData.postValue(UIState.Success(responseBody))

                            response.body()!!.data.id?.let {
                                SignInEntity(
                                    userId = it,
                                    signInData = response.body()!!.data.toString()
                                )
                            }?.let {
                                loginRepository.upsertSignInData(it)
                            }

                        } ?: run {
                            _loginData.postValue(UIState.Error(Exception("Empty response from server")))
                        }
                    } else {
                        val errorResponse = response.errorBody()?.let { errorBody ->
                            val errorMessage = errorBody.string()
                            val apiError = Gson().fromJson(errorMessage, SignInModel::class.java)
                            apiError.status ?: "Unknown error"
                        } ?: "Unknown error"
                        _loginData.postValue(UIState.Error(Exception("Error ${response.code()}: $errorResponse")))
                    }

                } catch (e: Exception) {
                    _loginData.postValue(UIState.Error(e))
                }
            } else {
                _loginData.postValue(UIState.Error(Exception("No internet connection")))
            }
        }
    }
}