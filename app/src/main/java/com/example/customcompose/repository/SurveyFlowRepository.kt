package com.example.customcompose.repository

import com.example.customcompose.app_database.dao.LocalDbDao
import com.example.customcompose.app_database.entity.PtrProgressEntity
import com.example.customcompose.model.GiveAbleAchievement
import com.example.customcompose.model.NumberCheckModel
import com.example.customcompose.network.ApiService
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class SurveyFlowRepository(
    private val apiService: ApiService,
    private val localDbDao: LocalDbDao
) {

    suspend fun checkNumber(token: String, requestBody: HashMap<String, Any>): Response<NumberCheckModel> {
        return apiService.checkNumber(token, requestBody)
    }

    suspend fun getAchievementData(token: String, id: String): Response<GiveAbleAchievement>{
        return apiService.getGiveAbleAchievement(token, id)
    }

    suspend fun sendOtp(token: String, requestBody: HashMap<String, Any>):Response<JsonObject>{
        return apiService.sendOTP(requestBody, token)
    }




    //=======================get local data===========================//
    suspend fun upsertPtrData(ptrProgressEntity: PtrProgressEntity){
        localDbDao.upsertPtrData(ptrProgressEntity)
    }

    fun getPtrData(brId: String, campId: String): Flow<PtrProgressEntity?> {
        return localDbDao.getPtrData(brId, campId)
    }

}