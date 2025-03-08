package com.example.customcompose.model

data class UserInfoModel(
    val status: String,
    val message: String,
    val data: List<UserDataModel>
)

data class UserDataModel(
    val name: String,
    val user_image: String,
    val desigantion: String,
    val locations: String,
    val agency_name: String,
    val org_name: String,
    val geo_info: GeoFence
)

data class GeoFence(
    val lat: Double,
    val long: Double,
    val forced: Boolean,
    val radius: Int,
    val status: Boolean
)