package com.vanpra.amblor.ui.layouts.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.vanpra.amblor.Screen
import com.vanpra.amblor.auth.AuthenticationApi
import com.vanpra.amblor.auth.EmailAlreadyRegistered
import com.vanpra.amblor.auth.InvalidPassword
import com.vanpra.amblor.repositories.AmblorApiRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application,
    private val apiRepo: AmblorApiRepository,
    private val auth: AuthenticationApi
) :
    AndroidViewModel(application) {
    fun signupWithEmail(
        signupModel: EmailSignupModel,
        navHostController: NavHostController
    ) = viewModelScope.launch {
        try {
            auth.createUserWithEmail(signupModel.email.text, signupModel.password.text)
            if (apiRepo.signupEmailUser(signupModel, auth.getToken()!!)) {
                navHostController.navigate(Screen.App.route)
            } else {
                auth.deleteCurrentUser()
            }
        } catch (e: EmailAlreadyRegistered) {
            signupModel.email.showError("Email already used")
        }
    }

    fun signInWithEmail(
        loginModel: LoginModel,
        navHostController: NavHostController
    ) = viewModelScope.launch {
        println(loginModel)
        val signInMethods = auth.fetchSignInMethodsForEmail(loginModel.email.text)

        if (signInMethods.isEmpty()) {
            loginModel.email.showError("Email not registered")
        } else if (!signInMethods.contains("password")) {
            loginModel.email.showError("Email registered with Google")
        } else {
            try {
                auth.signInWithEmailAndPassword(loginModel.email.text, loginModel.password.text)
                navHostController.navigate(Screen.App.route)
            } catch (e: InvalidPassword) {
                loginModel.password.showError("Invalid Password")
            }
        }
    }
}
