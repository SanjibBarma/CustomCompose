package com.example.customcompose.repository

import com.example.customcompose.app_database.dao.LocalDbDao
import com.example.customcompose.app_database.entity.SignInEntity
import com.example.customcompose.app_database.entity.SurveyDataEntity
import com.example.customcompose.model.CampaignsModel
import com.example.customcompose.model.SignInModel
import com.example.customcompose.model.SurveyData
import com.example.customcompose.model.SurveyModel
import com.example.customcompose.model.UserInfoModel
import com.example.customcompose.network.ApiService
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class LoginRepository(private val apiService: ApiService, private val localDbDao: LocalDbDao) {
    //api call will start from here
    //login api
    suspend fun getLoginInfo(signInMap: HashMap<String, Any>): Response<SignInModel> {
        return apiService.getSignInInfo(signInMap)
    }

    //get user api
    suspend fun getUserINfo(token: String): Response<UserInfoModel>{
        return apiService.getUserInfo(token)
    }

    //get campaign list
    suspend fun getCampaignList(token: String): Response<CampaignsModel>{
        return apiService.getBrCampList(token)
    }

    //get campaign data
    suspend fun getSurveyData(token: String, id: String): Response<SurveyData>{
        return apiService.getSurveyData(token, id)
    }







//=============================********************==================================//
    //local database will start from here
    //sign-in data
    suspend fun upsertSignInData(signInEntity: SignInEntity){
        localDbDao.upsertSignInData(signInEntity)
    }

    suspend fun getSignInDataByUserId(id: Int): SignInEntity?{
        return localDbDao.getSignInDataById(id)
    }


    //campaign data
    suspend fun upsertCampaignData(surveyDataEntity: SurveyDataEntity){
        localDbDao.upsertSurveyData(surveyDataEntity);
    }

    fun getSurveyDataDataByIds(brId: String, campId: String): Flow<SurveyDataEntity?> {
        return localDbDao.getSurveyDataDataByIds(brId, campId)
    }


}