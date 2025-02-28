package com.example.customcompose.model

data class RoutePlanListData(
    val typeTitle: String,
    val listPosition: Int,
    val locationList: List<RoutePlanData>?
)

data class RoutePlanData(
    val id: Int,
    val name: String,
    val type_slug: String,
    val parent: Int,
    val type: Int,
    val lat: Int,
    val lng: Int,
    val locations: List<RoutePlanData>?,
    var surveyHistoryModel: List<SurveyHistoryModel?>
)


const val LOCATION_STRING = """[{"id":88261,"name":"Alam Market, Gulshan-1","parent":5001,"type":7,"type_slug":"Cluster","lat":0,"long":0,"locations":[{"id":268309,"name":"Haider Store","parent":88261,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":268303,"name":"Kamal Store","parent":88261,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":268307,"name":"Mannan Store","parent":88261,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":268312,"name":"Rashed Store","parent":88261,"type":8,"type_slug":"Outlet","lat":0,"long":0}]},{"id":88262,"name":"Alam Market, Gulshan-2","parent":5001,"type":7,"type_slug":"Cluster","lat":0,"long":0,"locations":[{"id":268330,"name":"Alam Store","parent":88262,"type":8,"type_slug":"Outlet","lat":0,"long":0}]}]""";