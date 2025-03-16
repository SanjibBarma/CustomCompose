package com.example.customcompose.app_database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "PTR_PROGRESS_TABLE",
    indices = [Index(value = ["campId"], unique = true)]
)
data class PtrProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val campId: String,
    val brId: String,
    val ptrData: String
)
