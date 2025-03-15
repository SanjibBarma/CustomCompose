package com.example.customcompose.app_database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "target_achievement_table",
    indices = [Index(value = ["target_id", "user_id", "campaign_id"], unique = true)]
)
data class TargetAchievementEntity (
    @PrimaryKey(autoGenerate = false)
    val target_achievement: String,
    val target_id: Int,
    val user_id: String,
    val campaign_id: String
)