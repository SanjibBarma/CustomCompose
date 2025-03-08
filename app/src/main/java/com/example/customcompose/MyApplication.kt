package com.example.customcompose

import android.app.Application
import android.content.Context
import com.example.customcompose.helper.AppSessionManager
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.model.RoutePlanData
import com.example.customcompose.model.SURVEY_FLOW_JSON
import com.example.customcompose.model.LOCATION_STRING
import com.google.gson.Gson

class MyApplication : Application() {

    companion object {
        private lateinit var instance: MyApplication
    }


    override fun onCreate() {
        super.onCreate()
        instance = this

//        AppSessionManager(instance.applicationContext)
    }
}
