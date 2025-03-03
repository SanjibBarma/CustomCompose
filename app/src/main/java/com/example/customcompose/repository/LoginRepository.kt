package com.example.customcompose.repository

import com.example.customcompose.model.SignInModel
import com.example.customcompose.network.ApiService
import retrofit2.Response

class LoginRepository(private val apiService: ApiService) {
    suspend fun getLoginInfo(signInMap: HashMap<String, Any>): Response<SignInModel> {
        return apiService.getSignInInfo(signInMap)
    }
}