package com.vanpra.amblor.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.navigation.NavHost
import androidx.compose.navigation.composable
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.navigation.NavHostController
import com.vanpra.amblor.ui.layouts.login.EmailSignup
import com.vanpra.amblor.ui.layouts.login.GoogleSignup
import com.vanpra.amblor.ui.layouts.login.LoginLayout

enum class LoginNavigationState {
    Login,
    GoogleSignUp,
    EmailSignUp
}


@ExperimentalFocus
@ExperimentalAnimationApi
@Composable
fun LoginController() {
    NavHost(startDestination = LoginNavigationState.Login) {
        composable(LoginNavigationState.Login) { LoginLayout() }
        composable(LoginNavigationState.GoogleSignUp) { GoogleSignup() }
        composable(LoginNavigationState.EmailSignUp) { EmailSignup() }
    }
}