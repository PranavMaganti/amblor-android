package com.vanpra.amblor.ui.layouts.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.vanpra.amblor.BuildConfig
import com.vanpra.amblor.Screen
import com.vanpra.amblor.auth.AmblorUserApi
import com.vanpra.amblor.auth.AuthenticationApi
import com.vanpra.amblor.auth.InvalidPasswordException
import com.vanpra.amblor.auth.UserAlreadyRegisteredException
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application,
    private val apiRepo: AmblorUserApi,
    private val auth: AuthenticationApi
) : AndroidViewModel(application) {
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.REQUEST_ID_TOKEN)
        .requestEmail()
        .build()
    var client: GoogleSignInClient = GoogleSignIn.getClient(application.applicationContext, gso)

    val loginState = LoginState()
    val signupState = EmailSignupModel()

    fun signOut(mainNavHostController: NavHostController) {
        auth.signOut()
        client.signOut().addOnCompleteListener {
            mainNavHostController.navigate(Screen.Login.route)
        }
    }

    fun signupWithEmail(navHostController: NavHostController) = viewModelScope.launch {
        navHostController.navigate(Screen.Loading.route)
        try {
            auth.createUserWithEmail(signupState.email.text, signupState.password.text)
            if (apiRepo.signupEmailUser(signupState, auth.getToken()!!)) {
                navHostController.navigate(Screen.App.route)
            } else {
                auth.deleteCurrentUser()
                navHostController.navigate(Screen.EmailSignUp.route)
            }
        } catch (e: UserAlreadyRegisteredException) {
            signupState.email.showError("Email already used")
            navHostController.navigate(Screen.EmailSignUp.route)
        }
    }

    fun signInWithEmail(navHostController: NavHostController) = viewModelScope.launch {
        navHostController.navigate(Screen.Loading.route)
        val signInMethods = auth.fetchSignInMethodsForEmail(loginState.email.text)

        when {
            signInMethods.isEmpty() -> {
                loginState.email.showError("Email not registered")
                navHostController.navigate(Screen.Login.route)
            }
            !signInMethods.contains("password") -> {
                loginState.email.showError("Email registered with different provider")
                navHostController.navigate(Screen.Login.route)
            }
            else -> {
                try {
                    auth.signInWithEmailAndPassword(loginState.email.text, loginState.password.text)
                    navHostController.navigate(Screen.App.route)
                } catch (e: InvalidPasswordException) {
                    loginState.password.showError("Invalid Password")
                    navHostController.navigate(Screen.Login.route)
                }
            }
        }
    }
}
