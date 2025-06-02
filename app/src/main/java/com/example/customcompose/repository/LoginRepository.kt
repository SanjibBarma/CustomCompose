package com.example.customcompose.repository

import com.example.customcompose.storage.dao.LocalDbDao
import com.example.customcompose.storage.entity.SignInEntity
import com.example.customcompose.storage.entity.SurveyDataEntity
import com.example.customcompose.model.CampaignsModel
import com.example.customcompose.model.SignInModel
import com.example.customcompose.model.SurveyData
import com.example.customcompose.model.UserInfoModel
import com.example.customcompose.MyApplication.Companion.apiService
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class LoginRepository(private val localDbDao: LocalDbDao) {
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

    suspend fun getSignInDataByUserId(id: String): SignInEntity?{
        return localDbDao.getSignInDataById(id)
    }


    //campaign data
    suspend fun upsertCampaignData(surveyDataEntity: SurveyDataEntity){
        localDbDao.upsertSurveyData(surveyDataEntity);
    }

    fun getSurveyDataDataByIds(brId: String, campId: String): Flow<SurveyDataEntity?> {
        return localDbDao.getSurveyDataByIds(brId, campId)
    }


}