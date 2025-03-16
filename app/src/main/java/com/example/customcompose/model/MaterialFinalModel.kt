package com.example.customcompose.model

data class MaterialFinalModel (
     val id: Int?,
    val name: String?,
    val qty: Int?,
    val achievement: Int?,
    val type: Int?,
    val typeName: String?,
    val img_url: String?
)

data class GiveAbleAchievement(
    val status: String,
    val message: String,
    var data: List<MaterialFinalModel>
)


const val MATERIAL_STRING = """[{"id":12,"name":"Tea","qty":10,"type_name":"physical-giveable","type":40,"achievement":0},{"id":11,"name":"Swapping","qty":10,"type_name":"physical-giveable","type":40,"achievement":0}]""";
