package com.example.customcompose.app_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.customcompose.app_database.entity.SignInEntity
import com.example.customcompose.app_database.entity.SurveyDataEntity

@Dao
interface LocalDbDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSignInData(user: SignInEntity)

    @Query("SELECT * FROM signin_data_table WHERE userId = :id")
    suspend fun getSignInDataById(id: Int): SignInEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSurveyData(surveyData: SurveyDataEntity)

    @Query("SELECT * FROM CAMPAIGN_Table WHERE brId = :brId AND campId = :campId LIMIT 1")
    suspend fun getSurveyDataDataByIds(brId: String, campId: String): SurveyDataEntity

}