package com.example.customcompose.helper

import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.model.SurveyModel

object Constants {
    const val FAILED: Int = 34
    const val SUCCESSFUL: Int = 35

    var surveyFlowData: List<SurveyDataModel>? = null
    var fullCampaignData: SurveyModel? = null

    //clear the map after the data submission surveyBasicInfo.clear()
    val surveyBasicInfo: HashMap<String, Any> = hashMapOf()
}