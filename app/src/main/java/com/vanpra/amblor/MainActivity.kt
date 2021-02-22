package com.vanpra.amblor

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.navigation
import androidx.navigation.compose.popUpTo
import androidx.navigation.compose.rememberNavController
import com.vanpra.amblor.service.AmblorService
import com.vanpra.amblor.ui.layouts.MainAppLayout
import com.vanpra.amblor.ui.layouts.auth.AuthViewModel
import com.vanpra.amblor.ui.layouts.auth.EmailSignup
import com.vanpra.amblor.ui.layouts.auth.EmailVerification
import com.vanpra.amblor.ui.layouts.auth.GoogleSignup
import com.vanpra.amblor.ui.layouts.auth.LoginLayout
import com.vanpra.amblor.ui.theme.AmblorTheme
import com.vanpra.amblor.util.getViewModel
import dev.chrisbanes.accompanist.insets.systemBarsPadding
import org.koin.core.component.KoinApiExtension
import org.koin.core.parameter.parametersOf

sealed class Screen(val route: String) {
    object App : Screen("App")
    object Scrobbles : Screen("Scrobbles")
    object Stats : Screen("Stats")
    object Profile : Screen("Profile")
    object Login : Screen("Login")
    object GoogleSignUp : Screen("GoogleSignUp")
    object EmailSignUp : Screen("EmailSignUp")
    object EmailVerification : Screen("EmailVerification")
    object Auth : Screen("auth")
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent { MainLayout() }
    }

    @KoinApiExtension
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

@Composable
fun MainLayout(startScreen: Screen = Screen.Auth) {
    AmblorTheme {
        val navHostController = rememberNavController()
        val authViewModel = getViewModel<AuthViewModel> { parametersOf(navHostController) }

        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .systemBarsPadding()
        ) {
            NavHost(
                navController = navHostController,
                startDestination = startScreen.route
            ) {
                navigation(Screen.Login.route, Screen.Auth.route) {
                    composable(Screen.Login.route) { LoginLayout(authViewModel) }
                    composable(Screen.GoogleSignUp.route) { GoogleSignup(authViewModel) }
                    composable(Screen.EmailSignUp.route) { EmailSignup(authViewModel) }
                    composable(Screen.EmailVerification.route) { EmailVerification(authViewModel) }
                }
                composable(Screen.App.route) {
                    MainAppLayout {
                        navHostController.navigate(Screen.Auth.route) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
        }
    }
}
