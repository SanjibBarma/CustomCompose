package com.example.customcompose.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signin_data_table")
data class SignInEntity (
    @PrimaryKey(autoGenerate = false)
    val userId: String,
    val signInData: String
)