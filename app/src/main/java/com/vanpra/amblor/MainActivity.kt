package com.vanpra.amblor

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.vanpra.amblor.interfaces.AuthenticationApi
import com.vanpra.amblor.interfaces.AmblorApi
import com.vanpra.amblor.service.AmblorService
import com.vanpra.amblor.ui.controllers.MainAppLayout
import com.vanpra.amblor.ui.layouts.auth.AuthViewModel
import com.vanpra.amblor.ui.layouts.auth.EmailSignup
import com.vanpra.amblor.ui.layouts.auth.EmailVerification
import com.vanpra.amblor.ui.layouts.auth.GoogleSignup
import com.vanpra.amblor.ui.layouts.auth.LoginLayout
import com.vanpra.amblor.ui.theme.AmblorTheme
import com.vanpra.amblor.util.LoadingScreen
import dev.chrisbanes.accompanist.insets.systemBarsPadding
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.getViewModel
import org.koin.core.component.KoinApiExtension
import org.koin.core.parameter.parametersOf

val AmbientNavHostController = staticAmbientOf<NavHostController>()

sealed class Screen(val route: String) {
    object App : Screen("App")
    object Scrobbles : Screen("Scrobbles")
    object Stats : Screen("Stats")
    object Profile : Screen("Profile")
    object Login : Screen("Login")
    object GoogleSignUp : Screen("GoogleSignUp")
    object EmailSignUp : Screen("EmailSignUp")
    object EmailVerification : Screen("EmailVerification")
    object Loading : Screen("Loading")
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val auth by inject<AuthenticationApi>()
        val userApi by inject<AmblorApi>()

        setContent {
            val coroutineScope = rememberCoroutineScope()
            val navHostController = rememberNavController()

            coroutineScope.launch {
                if (!auth.isUserSignedIn()) {
                    navHostController.navigate(Screen.Login.route)
                } else if (!auth.isUserEmailVerified()) {
                    navHostController.navigate(Screen.EmailVerification.route)
                } else if (!userApi.isUserRegistered(auth.getToken())) {
                    navHostController.navigate(Screen.GoogleSignUp.route)
                } else {
                    navHostController.navigate(Screen.App.route)
                }
            }

            MainLayout(Screen.Loading, navHostController)
        }
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
fun MainLayout(
    startScreen: Screen = Screen.Login,
    navHostController: NavHostController = rememberNavController()
) {
    AmblorTheme {
        val authViewModel = getViewModel<AuthViewModel> { parametersOf(navHostController) }

        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .systemBarsPadding()
        ) {
            Providers(AmbientNavHostController provides navHostController) {
                NavHost(
                    navController = navHostController,
                    startDestination = startScreen.route
                ) {
                    composable(Screen.App.route) { MainAppLayout(authViewModel) }
                    composable(Screen.Login.route) { LoginLayout(authViewModel) }
                    composable(Screen.GoogleSignUp.route) { GoogleSignup(authViewModel) }
                    composable(Screen.EmailSignUp.route) { EmailSignup(authViewModel) }
                    composable(Screen.Loading.route) { LoadingScreen() }
                    composable(Screen.EmailVerification.route) { EmailVerification(authViewModel) }
                }
            }
        }
    }
}
