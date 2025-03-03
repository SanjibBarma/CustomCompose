package com.example.customcompose.model.number_validation

import com.example.customcompose.model.MaterialFinalModel


data class NumberCheckModel(
    val status: String,
    val message: String,
    val data: List<NumberCheckData>
)

data class NumberCheckData(
    val exist: Boolean?,
    val eligible: Boolean?,
    val information: List<DynamicInfoConModel>?,
    val status: Int?,
    val message: List<String>?,
    val materials: List<MaterialFinalModel>?
)

data class DynamicInfoConModel(
    val key: String,
    val value: String
)