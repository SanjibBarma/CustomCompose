package com.example.customcompose.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.customcompose.storage.dao.LocalDbDao
import com.example.customcompose.storage.entity.PtrProgressEntity
import com.example.customcompose.storage.entity.SignInEntity
import com.example.customcompose.storage.entity.SurveyDataEntity
import com.example.customcompose.storage.entity.TargetAchievementEntity

@Database(
    entities = [
        SignInEntity::class,
        SurveyDataEntity::class,
        TargetAchievementEntity::class,
        PtrProgressEntity::class
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
                    "ecrm_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}