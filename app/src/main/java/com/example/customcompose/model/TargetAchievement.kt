package com.example.customcompose.model

data class TargetAchievement(
    val id: Int,
    val user_id: Int,
    val locations: List<Location>,
    val primary_brands: List<Product>,
    val secondary_brands: List<Product>,
    val previous_brands: List<Product>,
    val daily_target: Int,
    val over_achivement: Boolean,
    val additional_kpi: List<AdditionalKpi>,
    var daily_achievement: Int,
    val total_achievement: Int
)

data class Location(
    val id: Int,
    val name: String
)

data class Product(
    val id: Int,
    val name: String
)

data class AdditionalKpi(
    val identifier: String,
    val target: Int,
    val achievement: Int
)

data class AchievementData(
    val status: String,
    val message: String,
    val data: AchievementModel,
)

data class AchievementModel(
    val targetAchievements: List<TargetAchievement>,
    val appVersion: AppVersion,
    val material_assignment: Boolean
)
