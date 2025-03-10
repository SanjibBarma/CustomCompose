package com.example.customcompose.navigation

sealed class Screen (val route: String){
    object LoginScreen: Screen("login_screen")
    object DynamicScreen: Screen("dynamic_screen")
    object DashboardScreen: Screen("dashboard_screen")

//    fun withArgs(vararg args: String): String{
//        return buildString {
//            append(route)
//            args.forEach { args->
//                append("/$args")
//            }
//        }
//    }
}