package com.vanpra.amblor.repositories

import android.content.Context
import android.content.Intent
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vanpra.amblor.BuildConfig
import com.vanpra.amblor.Screen
import com.vanpra.amblor.models.NewUser
import com.vanpra.amblor.ui.layouts.login.EmailSignupModel
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthRepository(context: Context, private val mainNavController: NavHostController) {
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.REQUEST_ID_TOKEN)
        .requestEmail()
        .build()
    var auth: FirebaseAuth = Firebase.auth
    var client: GoogleSignInClient = GoogleSignIn.getClient(context.applicationContext, gso)

    private val amblorApi = AmblorApiRepository.getInstance(context.applicationContext)

    suspend fun firebaseAuthWithGoogle(data: Intent, onNewUser: () -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data).await()
        val credential = GoogleAuthProvider.getCredential(task.idToken, null)
        val user = auth.signInWithCredential(credential).await().user!!
        val tokenId = user.getIdToken(true).await().token!!
        if (amblorApi.isNewUser(tokenId)) {
            onNewUser()
        } else {
            mainNavController.navigate(route = Screen.App.title)
        }
    }

    suspend fun addNewUser(username: String, onConflict: suspend () -> Unit) {
        // Will always be not null as this function is only called when signed in
        val user = auth.currentUser!!
        val tokenId = user.getIdToken(true).await().token!!
        val res = amblorApi.addUser(NewUser(username, tokenId))
        if (res.status == HttpStatusCode.Conflict) {
            onConflict()
        } else {
            mainNavController.navigate(route = Screen.App.title)
        }
    }

    fun emailSignup(signupModel: EmailSignupModel) {
        auth.createUserWithEmailAndPassword(signupModel.email, signupModel.password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    CoroutineScope(Dispatchers.IO).launch {
                        addNewUser(signupModel.username) {
                            signupModel.usernameUsed = true
                            Firebase.auth.currentUser!!.delete().await()
                        }
                    }
                } else if (it.exception is FirebaseAuthUserCollisionException) {
                    signupModel.emailUsed = true
                }
            }
    }

    fun emailLogin(username: String, password: String) {
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    mainNavController.navigate(route = Screen.App.title)
                } else {
                    println(it.exception)
                }
            }
    }

    // Non supending as this is called from activity onDestroy
    fun googleSignOut() {
        Firebase.auth.signOut()
        client.signOut().addOnCompleteListener {
            mainNavController.navigate(route = Screen.Login.title)
        }
    }
}