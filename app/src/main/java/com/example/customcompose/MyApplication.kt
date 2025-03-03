package com.example.customcompose

import android.app.Application
import android.content.Context
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.model.RoutePlanData
import com.example.customcompose.model.SURVEY_FLOW_JSON
import com.example.customcompose.model.LOCATION_STRING
import com.google.gson.Gson

class MyApplication : Application() {

    companion object {
        private lateinit var instance: MyApplication

        fun getAppContext(): Context {
            return instance.applicationContext
        }

        fun getSurveyDataModelList(): List<SurveyDataModel> {
            return instance.surveyDataModelList
        }

        fun getRoutePlanList(): List<RoutePlanData> {
            return instance.routePlanList
        }
    }

    private val gson = Gson()
    val surveyDataModelList: List<SurveyDataModel> by lazy {
        gson.fromJson(SURVEY_FLOW_JSON, Array<SurveyDataModel>::class.java).toList()
    }

    val routePlanList: List<RoutePlanData> by lazy {
        gson.fromJson(LOCATION_STRING, Array<RoutePlanData>::class.java).toList()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
