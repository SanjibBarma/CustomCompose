package com.example.customcompose.app_database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signin_data_table")
data class SignInEntity (
    @PrimaryKey(autoGenerate = false)
    val userId: Int,
    val signInData: String
)