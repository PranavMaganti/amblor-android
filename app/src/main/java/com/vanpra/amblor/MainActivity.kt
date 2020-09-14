package com.vanpra.amblor

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.platform.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.firebase.FirebaseApp
import com.vanpra.amblor.service.AmblorApi
import com.vanpra.amblor.service.AmblorService
import com.vanpra.amblor.ui.AmblorApp
import com.vanpra.amblor.ui.layouts.login.LoginController
import com.vanpra.amblor.ui.theme.AmblorTheme

val ViewModelAmbient = ambientOf<SharedViewModel> { error("No viewmodel provided") }

class MainActivity : AppCompatActivity() {
    private lateinit var sharedViewModel: SharedViewModel

    @ExperimentalAnimationApi
    @ExperimentalFocus
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AmblorApi.initalise(PreferenceManager.getDefaultSharedPreferences(application))

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        setContent {
            AmblorTheme {
                Providers(ViewModelAmbient provides sharedViewModel) {
                    Crossfade(current = sharedViewModel.appState) {
                        when (it) {
                            AmblorState.Login -> LoginController()
                            AmblorState.App -> AmblorApp()
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

        val currentUser = sharedViewModel.auth.currentUser
        if (currentUser != null) {
            sharedViewModel.appState = AmblorState.App
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (sharedViewModel.appState == AmblorState.Login) {
            sharedViewModel.googleSignOut()
        }
    }
}
