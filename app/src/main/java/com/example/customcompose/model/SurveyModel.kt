package com.example.customcompose.model

data class SurveyData(
    val status: String,
    val message: String,
    val data: List<SurveyModel>,
)

data class SurveyModel(
    val id: Int,
    val name: String,
    val versions: Versions,
    val video: List<Video>?,
    val image: List<String>?,
    val target: Int,
    val products: List<ProductDataModel>?,
    val survey_flow: List<SurveyDataModel>,
    val conditions: Conditions,
    val locations: List<RoutePlanLocation>?,
    val route_plan: List<RoutePlanData>?,
    val consumer_location: List<RoutePlanData>?,
    val outlets: List<RetailOutlets>,
    val theme_config: ThemeConfig
)

data class RoutePlanLocation(
    val slug: String,
    val locations: String
)

data class Conditions(
    val audio: Boolean,
    val submit: Submit,
//    val geo-fence: GeoFence,
    val geo_fence: GeoFence,
    val over_achievement: Boolean,
    val fake_location_validation: Boolean,
    val segments: List<Segment>,
    val url_closed: Boolean
)

data class Submit(
    val failed_contact: List<List<FailedContact>>
)

data class FailedContact(
    val id: String,
    val answer: String
)

data class Segment(
    val status: Int,
    val referTo: ReferTo
)

data class RetailOutlets(
    val id: Int,
    val name: String,
    val parent: Int,
    val type: Int,
    val type_slug: String,
    val lat: Int,
    val long: Int,
    val outlet_info: OutletInfo
)

data class OutletInfo(
    val owner: String,
    val contact: String,
    val address: String,
    val is_contacted: Boolean,

)

data class ThemeConfig(
    val theme_color: ThemeColor,
    val theme_icon: ThemeIcon
)

data class ThemeIcon(
    val titlebar_image: String
)

data class ThemeColor(
    val primary_color: String,
    val primary_color_light: String,
    val question_color: String,
    val title_font_color: String,
    val status_bar_color: String,
    val retake_reload_color: String,
)

data class ProductDataModel(
    val name: String,
    val id: String,
)

data class Versions(
    val campaign: Int,
    val video: Int,
    val image: Int,
)

data class Video(
    val md5: String,
    val name: String,
)
