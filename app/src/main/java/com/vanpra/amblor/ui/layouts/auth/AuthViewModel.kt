package com.vanpra.amblor.ui.layouts.auth

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.vanpra.amblor.BuildConfig
import com.vanpra.amblor.Screen
import com.vanpra.amblor.data.AmblorDatabase
import com.vanpra.amblor.interfaces.AmblorApi
import com.vanpra.amblor.interfaces.AuthenticationApi
import com.vanpra.amblor.interfaces.InvalidPasswordException
import com.vanpra.amblor.interfaces.UserAlreadyRegisteredException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    application: Application,
    private val apiRepo: AmblorApi,
    private val auth: AuthenticationApi,
    private val amblorDatabase: AmblorDatabase,
    private val navHostController: NavHostController
) : AndroidViewModel(application) {
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.REQUEST_ID_TOKEN)
        .requestEmail()
        .build()
    var client: GoogleSignInClient = GoogleSignIn.getClient(application.applicationContext, gso)

    val loginState = LoginState()
    val signupState = EmailSignupState()
    val googleSignupState = TextInputState(label = "Username", defaultError = "Username Taken")

    fun signOut(deleteUser: Boolean = false, clearDB: Boolean = false) =
        viewModelScope.launch {
            if (deleteUser) {
                auth.deleteCurrentUser()
            } else {
                auth.signOut()
            }

            if (clearDB) {
                amblorDatabase.clearAllTables()
            }

            client.signOut().await()
            navHostController.navigate(Screen.Login.route)
        }

    fun signInWithGoogle(data: Intent) =
        viewModelScope.launch {
            navHostController.navigate(Screen.Loading.route)
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).await()
            val googleCredential = GoogleAuthProvider.getCredential(account.idToken, null)
            val isNewUser = auth.signInWithCredential(googleCredential)

            if (isNewUser) {
                navHostController.navigate(Screen.GoogleSignUp.route)
            } else {
                navHostController.navigate(Screen.App.route)
            }
        }

    fun signUpWithGoogle() =
        viewModelScope.launch {
            navHostController.navigate(Screen.Loading.route)
            if (apiRepo.signUpUser(googleSignupState.text, auth.getToken())) {
                navHostController.navigate(Screen.App.route)
            } else {
                googleSignupState.showError("Username Taken")
                navHostController.navigate(Screen.GoogleSignUp.route)
            }
        }

    fun signUpWithEmail() = viewModelScope.launch {
        navHostController.navigate(Screen.Loading.route)
        try {
            auth.createUserWithEmail(signupState.email.text, signupState.password.text)
            if (apiRepo.signUpUser(signupState.username.text, auth.getToken())) {
                auth.sendVerificationLink()
                navHostController.navigate(Screen.EmailVerification.route)
            } else {
                auth.deleteCurrentUser()
                signupState.username.showError("Username Taken")
                navHostController.navigate(Screen.EmailSignUp.route)
            }
        } catch (e: UserAlreadyRegisteredException) {
            signupState.email.showError("Email already used")
            navHostController.navigate(Screen.EmailSignUp.route)
        }
    }

    fun signInWithEmail() = viewModelScope.launch {
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
                    if (auth.isUserEmailVerified()) {
                        navHostController.navigate(Screen.App.route)
                    } else {
                        navHostController.navigate(Screen.EmailVerification.route)
                    }
                } catch (e: InvalidPasswordException) {
                    loginState.password.showError("Invalid Password")
                    navHostController.navigate(Screen.Login.route)
                }
            }
        }
    }

    fun getEmailAddress() = auth.getCurrentUser()

    fun sendVerificationCode() = viewModelScope.launch {
        auth.sendVerificationLink()
    }

    fun refreshAndCheckVerification() = viewModelScope.launch {
        navHostController.navigate(Screen.Loading.route)
        auth.refreshCurrentUser()
        if (auth.isUserEmailVerified()) {
            navHostController.navigate(Screen.App.route)
        } else {
            navHostController.navigate(Screen.EmailVerification.route)
        }
    }
}
