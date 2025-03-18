package com.example.customcompose.network

import com.example.customcompose.model.AchievementData
import com.example.customcompose.model.CampaignsModel
import com.example.customcompose.model.ExtraServiceModel
import com.example.customcompose.model.GiveAbleAchievement
import com.example.customcompose.model.NumberCheckModel
import com.example.customcompose.model.SignInModel
import com.example.customcompose.model.SurveyData
import com.example.customcompose.model.UserInfoModel
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Streaming
import retrofit2.http.Url

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

    //get login info
    @POST("/login-manager/api/v1/auth/signin")
    suspend fun getSignInInfo(
        @Body signInMap: HashMap<String, Any>?
    ): Response<SignInModel>

    //get userinfo
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/campaign-manager/api/v1/survey/get-user")
    suspend fun getUserInfo(
        @Header("Authorization") token: String?
    ): Response<UserInfoModel>

    // Get campaign list
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/campaign-manager/api/v1/survey/get-campaigns")
    suspend fun getBrCampList(
        @Header("Authorization") authToken: String?
    ): Response<CampaignsModel>

    // Get Survey Data
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/campaign-manager/api/v1/survey/get-config/{id}")
    suspend fun getSurveyData(
        @Header("Authorization") authToken: String?,
        @Path("id") id: String?
    ): Response<SurveyData>

    //get extra service
    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("campaign-manager/api/v1/survey/get-services")
    suspend fun getExtraService(
        @Body deviceMap: HashMap<String, Any>?,
        @Header("Authorization") token: String?
    ): Response<ExtraServiceModel>

    //get target achievement data
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/campaign-manager/api/v1/survey/get-achievement/{id}")
    suspend fun getAchievementInfo(
        @Header("Authorization") token: String?,
        @Path("id") id: String?
    ): Response<AchievementData>

    //download images
    @Streaming
    @GET
    suspend fun downloadFile(@Url fileUrl: String): Response<ResponseBody>

    //send otp
    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/campaign-manager/api/v1/verification/send-otp")
    suspend fun sendOTP(
        @Body otpMap: HashMap<String, Any>?,
        @Header("Authorization") token: String?
    ): Response<JsonObject>

}