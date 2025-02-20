package com.example.customcompose.model

import kotlinx.serialization.Serializable

@Serializable
data class BlockList(
    val position: Int,
    val group: SurveyDataModel,
    val block: Block,
    val surveyHistoryModel: List<SurveyHistoryModel?>,
)

@Serializable
data class SurveyHistoryModel(
    val question: String,
    val answer: String,
    val id: String,
)

data class NonRefDataCheck(
    val index: Int,
    val type: String,
    val isRequired: String,
    val position: Int

)
