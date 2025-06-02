package com.example.customcompose.model

data class TapAnalysisModel(
    val block_id: String,
    val question: String,
    val result: List<Result?>,
    val survey_start_time: String
)

data class Result(
    val option: String,
    val tap_time: String
)
