package com.example.customcompose.viewmodel

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customcompose.app_database.entity.SignInEntity
import com.example.customcompose.app_database.entity.SurveyDataEntity
import com.example.customcompose.helper.AppSessionManager
import com.example.customcompose.helper.ConnectivityObserver
import com.example.customcompose.helper.UIState
import com.example.customcompose.model.CampaignsModel
import com.example.customcompose.model.SignInModel
import com.example.customcompose.model.SurveyData
import com.example.customcompose.model.SurveyModel
import com.example.customcompose.model.UserInfoModel
import com.example.customcompose.repository.LoginRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val connectivityObserver: ConnectivityObserver
): ViewModel() {

    //login api
    private val _loginData = MutableLiveData<UIState<SignInModel>>(UIState.Loading)
    val loginData: LiveData<UIState<SignInModel>> = _loginData

    private val _userId = MutableLiveData<String>()

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
                                _userId.postValue(response.body()!!.data.id.toString())

                                SignInEntity(
                                    userId = it,
                                    signInData = response.body()!!.data.toString()
                                )
                            }?.let {
                                loginRepository.upsertSignInData(it)
                            }

                            //call get userinfo api
                            responseBody.data.token?.let { token ->
                                getUserInfo("bearer $token")
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

    //================================***********=============================//
    //get user info api
    private val _userData = MutableLiveData<UIState<UserInfoModel>>(UIState.Loading)
    val userData: LiveData<UIState<UserInfoModel>> = _userData

    private fun getUserInfo(token: String){
        viewModelScope.launch(Dispatchers.IO) {
            if (connectivityObserver.checkInternetConnection()) {
                _userData.postValue(UIState.Loading)
                try {
                    val response = loginRepository.getUserINfo(token)

                    if (response.isSuccessful) {
                        response.body()?.let { responseBody ->
                            _userData.postValue(UIState.Success(responseBody))

                            //call campaign list api
                            getCampaignList(token)

                        } ?: run {
                            _userData.postValue(UIState.Error(Exception("Empty response from server")))
                        }
                    } else {
                        val errorResponse = response.errorBody()?.let { errorBody ->
                            val errorMessage = errorBody.string()
                            val apiError = Gson().fromJson(errorMessage, UserInfoModel::class.java)
                            apiError.status ?: "Unknown error"
                        } ?: "Unknown error"
                        _userData.postValue(UIState.Error(Exception("Error ${response.code()}: $errorResponse")))
                    }

                }catch (e: Exception) {
                    _userData.postValue(UIState.Error(e))
                }
            }else{
                _userData.postValue(UIState.Error(Exception("No internet connection")))
            }
        }
    }

    //================================***********=============================//
    private val _campaignListData = MutableLiveData<UIState<CampaignsModel>>(UIState.Loading)
    val campaignListData: LiveData<UIState<CampaignsModel>> = _campaignListData

    private fun getCampaignList(token: String){
        viewModelScope.launch(Dispatchers.IO) {
            if (connectivityObserver.checkInternetConnection()) {
                _campaignListData.postValue(UIState.Loading)

                try {
                    val response = loginRepository.getCampaignList(token)

                    if (response.isSuccessful) {
                        response.body()?.let { responseBody ->
                            _campaignListData.postValue(UIState.Success(responseBody))

                            //call survey data api
                            val campaignIds = responseBody.data.map { it.id.toString() }
                            if (campaignIds.isNotEmpty()) {
                                getSurveyData(token, campaignIds, 0)
                            }

                        } ?: run {
                            _campaignListData.postValue(UIState.Error(Exception("Empty response from server")))
                        }
                    } else {
                        val errorResponse = response.errorBody()?.let { errorBody ->
                            val errorMessage = errorBody.string()
                            val apiError = Gson().fromJson(errorMessage, CampaignsModel::class.java)
                            apiError.status ?: "Unknown error"
                        } ?: "Unknown error"
                        _campaignListData.postValue(UIState.Error(Exception("Error ${response.code()}: $errorResponse")))
                    }

                }catch (e: Exception) {
                    _campaignListData.postValue(UIState.Error(e))
                }
            }else{
                _campaignListData.postValue(UIState.Error(Exception("No internet connection")))
            }
        }
    }

    //================================***********=============================//
    private val _surveyData = MutableLiveData<UIState<SurveyData>>(UIState.Loading)
    val surveyData: LiveData<UIState<SurveyData>> = _surveyData

    private fun getSurveyData(token: String, campaignIds: List<String>, index: Int) {
        if (index >= campaignIds.size) {
            //call another api if needed
            //getAnotherApiData(token)
            println("index is over getSurveyData is stopped: $index")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            if (connectivityObserver.checkInternetConnection()) {
                _surveyData.postValue(UIState.Loading)

                try {
                    val response = loginRepository.getSurveyData(token, campaignIds[index])

                    if (response.isSuccessful) {
                        response.body()?.let { responseBody ->
                            _surveyData.postValue(UIState.Success(responseBody))

                            println("responseBody ${responseBody.toString()}")
                            val gson = Gson()

                            val campData = _userId.value?.let {
                                SurveyDataEntity(
                                    brId = it,
                                    campId = campaignIds[index],
                                    campData = gson.toJson(responseBody.data[0])
                                )
                            }
                            if (campData != null) {
                                loginRepository.upsertCampaignData(campData)
                            }


                            //call the next campaign if exist
                            getSurveyData(token, campaignIds, index + 1)
                            println("index is over getSurveyData is running: $index")

                        } ?: run {
                            _surveyData.postValue(UIState.Error(Exception("Empty response from server")))
                        }
                    } else {
                        val errorResponse = response.errorBody()?.let { errorBody ->
                            val errorMessage = errorBody.string()
                            val apiError = Gson().fromJson(errorMessage, JsonObject::class.java)
                            apiError ?: "Unknown error"
                        } ?: "Unknown error"
                        _surveyData.postValue(UIState.Error(Exception("Error ${response.code()}: $errorResponse")))
                    }

                } catch (e: Exception) {
                    _surveyData.postValue(UIState.Error(e))
                }
            } else {
                _surveyData.postValue(UIState.Error(Exception("No internet connection")))
            }
        }
    }






    //======================************************============================//
    //*********************** get local data **********************************//
    //========================************************=========================//

    private val _localSurveyData = MutableLiveData<SurveyDataEntity>()
    val localSurveyData: LiveData<SurveyDataEntity> = _localSurveyData

    fun fetchSurveyDataByIds(brId: String, campId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository.getSurveyDataDataByIds(brId, campId)

            withContext(Dispatchers.Main) {
                _localSurveyData.value = result
                println("surveyDataState_repo: ${_localSurveyData.value}")
            }
        }
    }


}