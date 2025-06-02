package com.example.customcompose.repository

import com.example.customcompose.MyApplication.Companion.apiService
import com.example.customcompose.storage.dao.LocalDbDao
import com.example.customcompose.storage.entity.TargetAchievementEntity
import com.example.customcompose.model.AchievementData
import com.example.customcompose.model.ExtraServiceModel
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class DashboardRepository (private val localDbDao: LocalDbDao){

    suspend fun getExtraServiceData(token: String, requestBody: HashMap<String, Any>): Response<ExtraServiceModel> {
        return apiService.getExtraService(requestBody, token)
    }

    suspend fun getAchievementInfo(token: String, cmpId: String): Response<AchievementData>{
        return apiService.getAchievementInfo(token, cmpId)
    }



    //=================get local data=====================//
    suspend fun upsertTargetAchievementData(targetAchievementEntity: TargetAchievementEntity){
        localDbDao.upsertTargetAchievementData(targetAchievementEntity)
    }

    fun getTargetAchievementData(brId: String, campId: String): Flow<TargetAchievementEntity?> {
        return localDbDao.getAllTargetAchievementData(brId, campId)
    }

}