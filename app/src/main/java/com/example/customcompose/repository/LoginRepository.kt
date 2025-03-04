package com.example.customcompose.repository

import com.example.customcompose.app_database.dao.SignInDao
import com.example.customcompose.app_database.entity.SignInEntity
import com.example.customcompose.model.SignInModel
import com.example.customcompose.network.ApiService
import retrofit2.Response

class LoginRepository(private val apiService: ApiService, private val signInDao: SignInDao) {
    //api call will start from here
    suspend fun getLoginInfo(signInMap: HashMap<String, Any>): Response<SignInModel> {
        return apiService.getSignInInfo(signInMap)
    }








    //local database will start from here
    suspend fun upsertSignInData(signInEntity: SignInEntity){
        signInDao.upsertSignInData(signInEntity)
    }

    suspend fun getSignInDataByUserId(id: Int): SignInEntity?{
        return signInDao.getSignInDataById(id)
    }
}