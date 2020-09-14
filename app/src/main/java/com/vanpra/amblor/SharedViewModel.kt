package com.vanpra.amblor

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vanpra.amblor.data.AmblorDatabase
import com.vanpra.amblor.models.NewUser
import com.vanpra.amblor.service.AmblorApi
import com.vanpra.amblor.service.NotificationRepository
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class AmblorState {
    Login,
    App
}

enum class LoginState {
    SignIn,
    GoogleSignUp,
    EmailSignUp
}

enum class NavigationState {
    Scrobbles,
    Stats,
    Profile
}

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    var appState by mutableStateOf(AmblorState.Login)
    var loginState by mutableStateOf(LoginState.SignIn)
    var navigationState by mutableStateOf(NavigationState.Scrobbles)

    private val scrobbleDao =
        AmblorDatabase.getDatabase(application.applicationContext).scrobbleDao()

    var auth: FirebaseAuth
    var client: GoogleSignInClient
    var amblorApi: AmblorApi = AmblorApi.getInstance()

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.REQUEST_ID_TOKEN)
            .requestEmail()
            .build()
        auth = Firebase.auth
        client = GoogleSignIn.getClient(application, gso)

        val notificationRepository =
            NotificationRepository.getInstance(application.applicationContext)
        notificationRepository.amblorApi = amblorApi

        viewModelScope.launch {
            // Have to remove token and do it when logged in
            val lastScrobbleTime = scrobbleDao.getLastScrobbleTime() ?: 0
            val user = auth.currentUser
            if (user != null) {
                val idToken = user.getIdToken(true).await().token!!
                amblorApi.getToken(idToken)

                val scrobbles = amblorApi.getUserScrobbles(lastScrobbleTime)
                scrobbleDao.insertAll(scrobbles)
            }
        }
    }

    suspend fun firebaseAuthWithGoogle(data: Intent): Boolean {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data).await()
        val credential = GoogleAuthProvider.getCredential(task.idToken, null)
        val user = auth.signInWithCredential(credential).await().user!!
        val tokenId = user.getIdToken(true).await().token!!
        return amblorApi.isNewUser(tokenId)
    }

    suspend fun addNewUser(username: String, onConflict: suspend () -> Unit) {
        // Will always be not null as this function is only called when signed in
        val user = auth.currentUser!!
        val tokenId = user.getIdToken(true).await().token!!
        val res = amblorApi.addUser(NewUser(username, tokenId))

        if (res.status == HttpStatusCode.Conflict) {
            onConflict()
        } else {
            appState = AmblorState.App
            loginState = LoginState.SignIn
        }
    }

    fun googleSignOut() {
        Firebase.auth.signOut()
        client.signOut().addOnCompleteListener {
            appState = AmblorState.Login
            loginState = LoginState.SignIn
        }
    }
}
