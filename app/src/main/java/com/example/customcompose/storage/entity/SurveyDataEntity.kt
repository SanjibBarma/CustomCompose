package com.example.customcompose.storage.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "CAMPAIGN_Table", indices = [Index(value = ["campId"], unique = true)])
data class SurveyDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val campId: String,
    val brId: String,
    val campData: String
)
