package com.example.customcompose.app_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.customcompose.app_database.entity.SignInEntity

@Dao
interface SignInDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSignInData(user: SignInEntity)

    @Query("SELECT * FROM signin_data_table WHERE userId = :id")
    suspend fun getSignInDataById(id: Int): SignInEntity?


}