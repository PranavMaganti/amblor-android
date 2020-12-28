package com.vanpra.amblor

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Providers
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vanpra.amblor.service.AmblorService
import com.vanpra.amblor.ui.controllers.AppController
import com.vanpra.amblor.ui.layouts.auth.EmailSignup
import com.vanpra.amblor.ui.layouts.auth.GoogleSignup
import com.vanpra.amblor.ui.layouts.auth.LoginLayout
import com.vanpra.amblor.ui.theme.AmblorTheme
import dev.chrisbanes.accompanist.insets.systemBarsPadding

sealed class Screen(val route: String) {
    object App : Screen("App")
    object Scrobbles : Screen("Scrobbles")
    object Stats : Screen("Stats")
    object Profile : Screen("Profile")
    object Login : Screen("Login")
    object GoogleSignUp : Screen("GoogleSignUp")
    object EmailSignUp : Screen("EmailSignUp")
}

val AmbientNavHostController = staticAmbientOf<NavHostController>()

class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AmblorTheme {
                val mainNavController = rememberNavController()
                Box(
                    Modifier.fillMaxSize().background(MaterialTheme.colors.background)
                        .systemBarsPadding()
                ) {
                    Providers(AmbientNavHostController provides mainNavController) {
                        NavHost(
                            navController = mainNavController,
                            startDestination = Screen.Login.route
                        ) {
                            composable(Screen.App.route) { AppController() }
                            composable(Screen.Login.route) { LoginLayout() }
                            composable(Screen.GoogleSignUp.route) { GoogleSignup() }
                            composable(Screen.EmailSignUp.route) { EmailSignup() }
                        }
                    }
                }

                onCommit {
                    val currentUser = Firebase.auth.currentUser
                    if (currentUser != null) {
                        mainNavController.navigate(route = Screen.App.route)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        Intent(this, AmblorService::class.java).also { intent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }
}
