package com.example.mobilecomputingassignment

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilecomputingassignment.screen.AuthScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "auth", builder = {
        composable("auth"){
            AuthScreen(modifier)
        }
    })
}