package com.example.mobilecomputingassignment

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilecomputingassignment.model.EventModel
import com.example.mobilecomputingassignment.pages.EventDetailsPage
import com.example.mobilecomputingassignment.screen.AuthScreen
import com.example.mobilecomputingassignment.screen.HomeScreen
import com.example.mobilecomputingassignment.screen.LoginScreen
import com.example.mobilecomputingassignment.screen.NameChangeScreen
import com.example.mobilecomputingassignment.screen.SignupScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    GlobalNavigation.navController = navController

    /*
    For testing purposes you can change the value of isLoggedIn
    to either true for when the the user is connected with Firebase
    or to false when the user isn't connected.
    After the backend is completed change it.
    Also change AuthViewModel, in viewmodel by removing the comments and it should work
    as well as the Signinscreen and Loginscreen Buttons, by removing the comments at the onClick
    */
    val isLoggedIn = /*true*/Firebase.auth.currentUser!=null
    val fistPage = if(isLoggedIn) Routes.homescreen else Routes.authscreen

    NavHost(navController = navController, startDestination = fistPage, builder = {
        composable(Routes.authscreen){
            AuthScreen(modifier, navController)
        }
        composable(Routes.loginscreen) {
            LoginScreen(modifier, navController)
        }
        composable(Routes.signupscreen) {
            SignupScreen(modifier, navController)
        }
        composable(Routes.homescreen) {
            HomeScreen(modifier, navController)
        }
        composable(Routes.namechangescreen) {
            NameChangeScreen(modifier, navController)
        }
        composable(Routes.eventdetailspage+"{uid}") {
            var eventID = it.arguments?.getString("uid")
            EventDetailsPage(modifier, eventID?:"")
        }
    })
}

object GlobalNavigation{
    lateinit var navController : NavHostController
}