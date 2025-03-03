package com.example.customcompose.network

import com.example.customcompose.model.SignInModel
import com.example.customcompose.model.number_validation.GiveAbleAchievement
import com.example.customcompose.model.number_validation.NumberCheckModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/campaign-manager/api/v1/survey/check-number")
    suspend fun checkNumber(
        @Header("Authorization") token: String,
        @Body requestBody: HashMap<String, Any>
    ): Response<NumberCheckModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/campaign-manager/api/v1/survey/get-giveable-achievement/{id}")
    suspend fun getGiveAbleAchievement(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<GiveAbleAchievement>

    @POST("/login-manager/api/v1/auth/signin")
    suspend fun getSignInInfo(
        @Body signInMap: HashMap<String, Any>?
    ): Response<SignInModel>
}