package com.example.customcompose.model

import kotlinx.serialization.Serializable

@Serializable
data class BlockList(
    val position: Int,
    val group: SurveyDataModel,
    val block: Block,
    val surveyHistoryModel: SurveyHistoryModel?,
)

@Serializable
data class SurveyHistoryModel(
    val question: String,
    val answer: String,
    val id: String
)