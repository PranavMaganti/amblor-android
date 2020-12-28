package com.vanpra.amblor.repositories

import android.content.Context
import androidx.navigation.NavHostController

class AuthRepository(context: Context, private val mainNavController: NavHostController) {
    // private val amblorApi = AmblorApiRepository
    //
    // suspend fun firebaseAuthWithGoogle(data: Intent, onNewUser: () -> Unit) {
    //     val task = GoogleSignIn.getSignedInAccountFromIntent(data).await()
    //     val credential = GoogleAuthProvider.getCredential(task.idToken, null)
    //     val user = auth.signInWithCredential(credential).await().user!!
    //     val tokenId = user.getIdToken(true).await().token!!
    //     // if (amblorApi.isNewUser(tokenId)) {
    //     //     onNewUser()
    //     // } else {
    //     //     mainNavController.navigate(route = Screen.App.route)
    //     // }
    // }
    //
    // suspend fun addNewUser(username: String, onConflict: suspend () -> Unit) {
    //     // Will always be not null as this function is only called when signed in
    //     val user = auth.currentUser!!
    //     val tokenId = user.getIdToken(true).await().token!!
    //     // val res = amblorApi.addUser(NewUser(username, tokenId))
    //     // if (res.status == HttpStatusCode.Conflict) {
    //     //     onConflict()
    //     // } else {
    //     //     mainNavController.navigate(route = Screen.App.route)
    //     // }
    // }
    //
    // fun emailSignup(signupModel: EmailSignupModel) {
    //     auth.createUserWithEmailAndPassword(signupModel.email, signupModel.password)
    //         .addOnCompleteListener {
    //             if (it.isSuccessful) {
    //                 CoroutineScope(Dispatchers.IO).launch {
    //                     addNewUser(signupModel.username) {
    //                         signupModel.usernameUsed = true
    //                         Firebase.auth.currentUser!!.delete().await()
    //                     }
    //                 }
    //             } else if (it.exception is FirebaseAuthUserCollisionException) {
    //                 signupModel.emailUsed = true
    //             }
    //         }
    // }
    //
    // fun emailLogin(username: String, password: String) {
    //     auth.signInWithEmailAndPassword(username, password)
    //         .addOnCompleteListener {
    //             if (it.isSuccessful) {
    //                 mainNavController.navigate(route = Screen.App.route)
    //             } else {
    //                 println(it.exception)
    //             }
    //         }
    // }
    //
    // // Non supending as this is called from activity onDestroy
    // fun googleSignOut() {
    //     Firebase.auth.signOut()
    //     client.signOut().addOnCompleteListener {
    //         mainNavController.navigate(route = Screen.Login.route)
    //     }
    // }
}
