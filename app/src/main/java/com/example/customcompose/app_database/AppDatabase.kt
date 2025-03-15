package com.example.customcompose.app_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.customcompose.app_database.dao.LocalDbDao
import com.example.customcompose.app_database.entity.SignInEntity
import com.example.customcompose.app_database.entity.SurveyDataEntity
import com.example.customcompose.app_database.entity.TargetAchievementEntity

@Database(
    entities = [
        SignInEntity::class,
        SurveyDataEntity::class,
        TargetAchievementEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dbDao(): LocalDbDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}