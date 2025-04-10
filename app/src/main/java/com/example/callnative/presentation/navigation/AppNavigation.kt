package com.example.callnative.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.callnative.common.utils.NavigationUtils
import com.example.callnative.presentation.ui.MainScreen
import com.example.callnative.presentation.ui.call.CallScreen
import com.example.callnative.presentation.ui.call.NotiCallScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = MainScreen.ROUTE) {
        NavigationUtils.setNavController(navController)
        composable(route = MainScreen.ROUTE) {
            MainScreen.Screen()
        }
        composable(route = CallScreen.ROUTE) {
            CallScreen.Screen()
        }
    }
}
