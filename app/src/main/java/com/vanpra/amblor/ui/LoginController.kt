package com.vanpra.amblor.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vanpra.amblor.Screen
import com.vanpra.amblor.ui.layouts.login.EmailSignup
import com.vanpra.amblor.ui.layouts.login.GoogleSignup
import com.vanpra.amblor.ui.layouts.login.LoginLayout

@ExperimentalAnimationApi
@Composable
fun AuthController() {
    NavHost(navController = rememberNavController(), startDestination = Screen.Login.title) {
        composable(Screen.Login.title) { LoginLayout() }
        composable(Screen.GoogleSignUp.title) { GoogleSignup() }
        composable(Screen.EmailSignUp.title) { EmailSignup() }
    }
}