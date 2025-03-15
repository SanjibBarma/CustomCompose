package com.example.customcompose.model

data class ExtraServiceModel (
    val status: String,
    val message: String,
    val data: ExtraServiceData
)

data class ExtraServiceData(
    val block: Boolean,
    val appVersion: AppVersion,
    val campaign_info: List<ExtraServiceCampaignInfo>
)

data class ExtraServiceCampaignInfo(
    val id: Int,
    val version: Int,
    val image_version: Int,
    val video_version: Int,
    val material_assigned: Boolean,
)