package com.example.customcompose.app_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.customcompose.app_database.dao.SignInDao
import com.example.customcompose.app_database.entity.SignInEntity

@Database(entities = [SignInEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun signInDao(): SignInDao

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