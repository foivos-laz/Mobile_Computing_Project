package com.example.mobilecomputingassignment

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilecomputingassignment.screen.AuthScreen
import com.example.mobilecomputingassignment.screen.LoginScreen
import com.example.mobilecomputingassignment.screen.SignupScreen


@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.authscreen, builder = {
        composable(Routes.authscreen){
            AuthScreen(modifier, navController)
        }
        composable(Routes.loginscreen) {
            LoginScreen(modifier, navController)
        }
        composable(Routes.signupscreen) {
            SignupScreen(modifier, navController)
        }
    })
}