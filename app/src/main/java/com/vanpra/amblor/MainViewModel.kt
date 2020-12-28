package com.vanpra.amblor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.REQUEST_ID_TOKEN)
        .requestEmail()
        .build()
    var client: GoogleSignInClient = GoogleSignIn.getClient(application.applicationContext, gso)

    fun signOut(mainNavHostController: NavHostController) {
        Firebase.auth.signOut()
        client.signOut().addOnCompleteListener {
            mainNavHostController.navigate(Screen.Login.route)
        }
    }
}
