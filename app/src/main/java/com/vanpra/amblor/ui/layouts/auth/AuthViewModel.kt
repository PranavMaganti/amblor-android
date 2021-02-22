package com.vanpra.amblor.ui.layouts.auth

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.GoogleAuthProvider
import com.vanpra.amblor.Screen
import com.vanpra.amblor.interfaces.AmblorApi
import com.vanpra.amblor.interfaces.AuthenticationApi
import com.vanpra.amblor.interfaces.InvalidPasswordException
import com.vanpra.amblor.interfaces.UserAlreadyRegisteredException
import com.vanpra.amblor.util.TextInputState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthViewModel(
    application: Application,
    private val amblorApi: AmblorApi,
    private val auth: AuthenticationApi,
    val navHostController: NavHostController
) : AndroidViewModel(application) {
    fun loadingScreenNavigation() = viewModelScope.launch {
        if (!auth.isUserSignedIn()) {
            return@launch
        } else if (!auth.isUserEmailVerified()) {
            navHostController.navigate(Screen.EmailVerification.route)
        } else if (!amblorApi.isUserRegistered(auth.getToken())) {
            navHostController.navigate(Screen.GoogleSignUp.route)
        } else {
            navHostController.navigate(Screen.App.route) {
                popUpTo(Screen.Login.route) {
                    inclusive = true
                }
            }
        }
    }

    fun signOut(deleteUser: Boolean = false) =
        viewModelScope.launch(Dispatchers.IO) {
            if (deleteUser) {
                auth.deleteCurrentUser()
            } else {
                auth.signOut()
            }

            withContext(Dispatchers.Main) {
                navHostController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Login.route) {
                        inclusive = true
                    }
                }
            }
        }

    fun signInWithGoogle(data: Intent) =
        viewModelScope.launch {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).await()
            val googleCredential = GoogleAuthProvider.getCredential(account.idToken, null)
            val isNewUser = auth.signInWithCredential(googleCredential)

            if (isNewUser) {
                navHostController.navigate(Screen.GoogleSignUp.route)
            } else {
                navHostController.navigate(Screen.App.route) {
                    popUpTo(Screen.Login.route) {
                        inclusive = true
                    }
                }
            }
        }

    fun signUpWithGoogle(googleSignupState: TextInputState) =
        viewModelScope.launch {
            if (amblorApi.signUpUser(googleSignupState.text, auth.getToken())) {
                navHostController.navigate(Screen.App.route) {
                    popUpTo(Screen.Login.route) {
                        inclusive = true
                    }
                }
            } else {
                googleSignupState.showError("Username Taken")
                navHostController.navigate(Screen.GoogleSignUp.route)
            }
        }

    fun signUpWithEmail(email: TextInputState, username: TextInputState, password: TextInputState) =
        viewModelScope.launch {
            try {
                auth.createUserWithEmail(email.text, password.text)
                if (amblorApi.signUpUser(username.text, auth.getToken())) {
                    auth.sendVerificationLink()
                    navHostController.navigate(Screen.EmailVerification.route)
                } else {
                    auth.deleteCurrentUser()
                    username.showError("Username Taken")
                }
            } catch (e: UserAlreadyRegisteredException) {
                email.showError("Email already used")
            }
        }

    fun signInWithEmail(emailState: TextInputState, passwordState: TextInputState) =
        viewModelScope.launch {
            val signInMethods = auth.fetchSignInMethodsForEmail(emailState.text)

            when {
                signInMethods.isEmpty() -> {
                    emailState.showError("Email not registered")
                }
                !signInMethods.contains("password") -> {
                    emailState.showError("Email registered with different provider")
                }
                else -> {
                    try {
                        auth.signInWithEmailAndPassword(
                            emailState.text,
                            passwordState.text
                        )
                        if (auth.isUserEmailVerified()) {
                            navHostController.navigate(Screen.App.route) {
                                popUpTo(Screen.Login.route) {
                                    inclusive = true
                                }
                            }
                        } else {
                            navHostController.navigate(Screen.EmailVerification.route)
                        }
                    } catch (e: InvalidPasswordException) {
                        passwordState.showError("Invalid Password")
                    }
                }
            }
        }

    fun getEmailAddress() = auth.getCurrentUser()

    fun sendVerificationCode() = viewModelScope.launch {
        auth.sendVerificationLink()
    }

    fun refreshAndCheckVerification() = viewModelScope.launch {
        auth.refreshCurrentUser()
        if (auth.isUserEmailVerified()) {
            navHostController.navigate(Screen.App.route) {
                popUpTo(Screen.Login.route) {
                    inclusive = true
                }
            }
        } else {
            navHostController.navigate(Screen.EmailVerification.route)
        }
    }

    fun getGoogleSignInClient() = auth.googleClient
}
