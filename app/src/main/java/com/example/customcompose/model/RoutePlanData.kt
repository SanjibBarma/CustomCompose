package com.example.customcompose.model

data class RoutePlanParentModel(
    val typeTitle: String,
    val listPosition: Int,
    val locationList: List<RoutePlanData>?,
    var selectedId: Int? = 0,
)

data class RoutePlanData(
    val id: Int,
    val name: String,
    val type_slug: String,
    val parent: Int,
    val type: Int,
    val lat: Int,
    val lng: Int,
    val locations: List<RoutePlanData>?
)


const val LOCATION_STRING = """[{"id":88261,"name":"Alam Market, Gulshan-1","parent":5001,"type":7,"type_slug":"Cluster","lat":0,"long":0,"locations":[{"id":268309,"name":"Haider Store","parent":88261,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":268303,"name":"Kamal Store","parent":88261,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":268307,"name":"Mannan Store","parent":88261,"type":8,"type_slug":"Outlet","lat":0,"long":0,"locations":[{"id":1309,"name":"Haider Store","parent":2312,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":1303,"name":"Kamal Store","parent":2312,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":1307,"name":"Mannan Store","parent":2312,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":1312,"name":"Rashed Store","parent":2312,"type":8,"type_slug":"Outlet","lat":0,"long":0}]},{"id":268312,"name":"Rashed Store","parent":88261,"type":8,"type_slug":"Outlet","lat":0,"long":0,"locations":[{"id":26309,"name":"Haider Store","parent":268312,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":26303,"name":"Kamal Store","parent":268312,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":26307,"name":"Mannan Store","parent":268312,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":26312,"name":"Rashed Store","parent":268312,"type":8,"type_slug":"Outlet","lat":0,"long":0,"locations":[{"id":2309,"name":"Haider Store","parent":26312,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":2303,"name":"Kamal Store","parent":26312,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":2307,"name":"Mannan Store","parent":26312,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":2312,"name":"Rashed Store","parent":26312,"type":8,"type_slug":"Outlet","lat":0,"long":0,"locations":[{"id":309,"name":"Haider Store","parent":2312,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":303,"name":"Kamal Store","parent":2312,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":307,"name":"Mannan Store","parent":2312,"type":8,"type_slug":"Outlet","lat":0,"long":0},{"id":312,"name":"Rashed Store","parent":2312,"type":8,"type_slug":"Outlet","lat":0,"long":0}]}]}]}]},{"id":88262,"name":"Alam Market, Gulshan-2","parent":5001,"type":7,"type_slug":"Cluster","lat":0,"long":0,"locations":[{"id":268330,"name":"Alam Store","parent":88262,"type":8,"type_slug":"Outlet","lat":0,"long":0}]}]""";