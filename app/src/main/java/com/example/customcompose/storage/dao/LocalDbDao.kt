package com.example.customcompose.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.customcompose.storage.entity.PtrProgressEntity
import com.example.customcompose.storage.entity.SignInEntity
import com.example.customcompose.storage.entity.SurveyDataEntity
import com.example.customcompose.storage.entity.TargetAchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalDbDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSignInData(user: SignInEntity)

    @Query("SELECT * FROM signin_data_table WHERE userId = :id")
    suspend fun getSignInDataById(id: String): SignInEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSurveyData(surveyData: SurveyDataEntity)


    @Query("SELECT * FROM CAMPAIGN_Table WHERE brId = :brId AND campId = :campId LIMIT 1")
    fun getSurveyDataByIds(brId: String, campId: String): Flow<SurveyDataEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTargetAchievementData(targetAchievementEntity: TargetAchievementEntity)

    @Query("SELECT * FROM target_achievement_table WHERE user_id = :brId AND campaign_id = :campId")
    fun getAllTargetAchievementData(brId: String, campId: String): Flow<TargetAchievementEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPtrData(ptrProgressEntity: PtrProgressEntity)

    @Query("SELECT * FROM PTR_PROGRESS_TABLE WHERE brId = :brId AND campId = :campId")
    fun getPtrData(brId: String, campId: String): Flow<PtrProgressEntity?>

}