package com.vanpra.amblor.ui.layouts.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vanpra.amblor.Screen
import com.vanpra.amblor.repositories.AmblorApiRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    fun signupWithEmail(
        signupModel: EmailSignupModel,
        navHostController: NavHostController
    ) = viewModelScope.launch {
        try {
            val auth = Firebase.auth
                .createUserWithEmailAndPassword(signupModel.email.text, signupModel.password.text)
                .await()
            val idToken = auth.user!!.getIdToken(true).await().token!!
            if (AmblorApiRepository.signupEmailUser(signupModel, idToken)) {
                navHostController.navigate(Screen.App.route)
            } else {
                Firebase.auth.currentUser!!.delete().await()
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            signupModel.email.showError("Email already used")
        } catch (e: Exception) {
            println(e)
        }
    }

    fun signInWithEmail(
        loginModel: LoginModel,
        navHostController: NavHostController
    ) = viewModelScope.launch {
        println(loginModel)
        val signInMethods =
            Firebase.auth.fetchSignInMethodsForEmail(loginModel.email.text)
                .await().signInMethods!!

        if (signInMethods.isEmpty()) {
            loginModel.email.showError("Email not registered")
        } else if (!signInMethods.contains("password")) {
            loginModel.email.showError("Email registered with Google")
        } else {
            Firebase.auth.signInWithEmailAndPassword(
                loginModel.email.text,
                loginModel.password.text
            ).addOnCompleteListener {
                if (!it.isSuccessful) {
                    loginModel.password.showError("Invalid Password")
                } else {
                    navHostController.navigate(Screen.App.route)
                }
            }
        }
    }
}
