package com.example.customcompose.repository

import com.example.customcompose.model.number_validation.GiveAbleAchievement
import com.example.customcompose.model.number_validation.NumberCheckModel
import com.example.customcompose.network.ApiService
import retrofit2.Response

class NumberValidationRepository(private val apiService: ApiService) {

    suspend fun checkNumber(token: String, requestBody: HashMap<String, Any>): Response<NumberCheckModel> {
        return apiService.checkNumber(token, requestBody)
    }

    suspend fun getAchievementData(token: String, id: String): Response<GiveAbleAchievement>{
        return apiService.getGiveAbleAchievement(token, id)
    }

}