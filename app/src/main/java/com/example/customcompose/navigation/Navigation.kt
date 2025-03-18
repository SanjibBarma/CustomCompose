package com.example.customcompose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.customcompose.viewmodel.BlockListViewModel
import com.example.customcompose.viewmodel.LoginViewModel
import com.example.customcompose.viewmodel.SurveyFlowViewModel
import com.example.customcompose.views.screens.DashboardScreen
import com.example.customcompose.views.screens.DynamicScreen
import com.example.customcompose.views.screens.LoginScreen

@Composable
fun Navigation (
    blockListViewModel: BlockListViewModel,
    numberValidationViewModel: SurveyFlowViewModel,
    loginViewModel: LoginViewModel,
){

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {
        composable(route = Screen.LoginScreen.route){
            LoginScreen(navController)
        }

        composable(route = Screen.DynamicScreen.route){
            DynamicScreen(navController)
        }

        composable(route = Screen.DashboardScreen.route){
            DashboardScreen(navController)
        }
    }
}