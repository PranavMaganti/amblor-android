package com.vanpra.amblor

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.navigation.ComposeNavigator
import androidx.compose.navigation.NavHost
import androidx.compose.navigation.composable
import androidx.compose.navigation.navigate
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.onCommit
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.platform.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import com.vanpra.amblor.repositories.AuthRepository
import com.vanpra.amblor.service.AmblorService
import com.vanpra.amblor.ui.AppController
import com.vanpra.amblor.ui.LoginController
import com.vanpra.amblor.ui.theme.AmblorTheme
import com.vanpra.amblor.util.ProvideDisplayInsets
import com.vanpra.amblor.util.systemBarsPadding

val AuthAmbient = ambientOf<AuthRepository> { error("AuthAmbient not initialised") }

enum class MainNavigationState {
    Login,
    App
}

class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    @ExperimentalFocus
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val mainNavController = NavHostController(this).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
        }
        mainNavController.enableOnBackPressed(false)
        val authRepository = AuthRepository(applicationContext, mainNavController)

        setContent {
            Providers(AuthAmbient provides authRepository) {
                AmblorTheme {
                    ProvideDisplayInsets {
                        Box(
                            Modifier.fillMaxSize().background(MaterialTheme.colors.background)
                                .systemBarsPadding()
                        ) {
                            NavHost(
                                navController = mainNavController,
                                startDestination = MainNavigationState.Login
                            ) {
                                composable(MainNavigationState.Login) { LoginController() }
                                composable(MainNavigationState.App) { AppController() }
                            }
                        }


                        onCommit {
                            val currentUser = authRepository.auth.currentUser
                            if (currentUser != null) {
                                mainNavController.navigate(MainNavigationState.App)
                            }
                        }
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
