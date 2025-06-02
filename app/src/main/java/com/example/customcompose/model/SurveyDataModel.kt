package com.example.customcompose.model

import kotlinx.serialization.Serializable

@Serializable
data class SurveyDataModel(
    val type: String,
    val group: String,
    val group_name: String?,
    val blocks: List<Block>,
    val jumping_logic: List<JumpingLogic>
)

@Serializable
data class Block(
    val id: String?,
    val skip: Skip?,
    val type: String,
    val options: List<Option>?,
    val referTo: ReferTo?,
    val question: Question?,
    val required: String?,
    val validations: Validations?,
    val group: String?,
    val blocks: List<Block>?,
    var surveyHistoryModel: List<SurveyHistoryModel?>,
    val jumping_logic: List<JumpingLogic>?,
    var position: Int = 0
)

@Serializable
data class Skip(val id: String, val group_no: String)

@Serializable
data class Option(val alias: Int, val slug: String?, val value: String, val referTo: ReferTo?, val name: String?, val isChecked: Boolean = false)

@Serializable
data class ReferTo(val id: String?, val group_no: String?)

@Serializable
data class Question(val slug: String, var alias: String)

@Serializable
data class Validations(
    var regex: String?,
    var max: Int?,
    var min: Int?,
    var terms: List<String>?,
    var bypass: Boolean?,
    var device: Boolean?,
    var server: Boolean?,
    var editable: Boolean?,
    var prefix: String?,
    var partial: Boolean = false,
    var faceDetection: Boolean?,
    var instantUpload: Boolean?,
//    var irisTrack: IrisTrack?,
    var invisible: Boolean?,
    var faceFit: Boolean?
)

@Serializable
data class JumpingLogic(
    val id: String,
    val group_no: String,
    val conditions: List<JumpCondition>
)

@Serializable
data class JumpCondition(
    val id: String,
    val answer: String
)

@Serializable
data class SurveyHistoryModel(
    val question: String,
    val answer: String,
    val id: String,
)