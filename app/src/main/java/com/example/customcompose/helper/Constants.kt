package com.example.customcompose.helper

import com.example.customcompose.model.Result
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.model.SurveyModel

object Constants {
    const val FAILED: Int = 34
    const val SUCCESSFUL: Int = 35
    const val BTN_NXT: String = "next_button"

    var surveyFlowData: List<SurveyDataModel>? = null
    var fullCampaignData: SurveyModel? = null

    val surveyBasicInfo: HashMap<String, Any> = hashMapOf()
    var numberValTapResult: MutableList<Result?> = mutableListOf()

}