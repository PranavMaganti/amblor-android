package com.vanpra.amblor.repositories

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vanpra.amblor.interfaces.AuthenticationApi
import com.vanpra.amblor.interfaces.InvalidPasswordException
import com.vanpra.amblor.interfaces.UserAlreadyRegisteredException
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(override val googleClient: GoogleSignInClient) : AuthenticationApi {
    private val actionCodeSettings = ActionCodeSettings.newBuilder()
        .setUrl("https://amblor.page.link")
        .setHandleCodeInApp(true)
        .setAndroidPackageName("com.vanpra.amblor", true, "1")
        .build()

    override suspend fun createUserWithEmail(username: String, password: String) {
        try {
            Firebase.auth.createUserWithEmailAndPassword(username, password).await()
        } catch (e: FirebaseAuthUserCollisionException) {
            throw UserAlreadyRegisteredException("Email already used")
        } catch (e: Exception) {
            println(e)
        }
    }

    override suspend fun deleteCurrentUser() {
        Firebase.auth.currentUser!!.delete().await()
    }

    override suspend fun fetchSignInMethodsForEmail(email: String): List<String> {
        return Firebase.auth.fetchSignInMethodsForEmail(email).await().signInMethods!!
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String) {
        try {
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw InvalidPasswordException("Invalid password given")
        } catch (e: Exception) {
            println(e)
        }
    }

    override suspend fun getToken(): String {
        return Firebase.auth.currentUser!!.getIdToken(true).await().token!!
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
        googleClient.signOut().await()
    }

    override fun isUserSignedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override suspend fun signInWithCredential(credential: AuthCredential): Boolean {
        val authRes = Firebase.auth.signInWithCredential(credential).await()
        return authRes.additionalUserInfo!!.isNewUser
    }

    override fun isUserEmailVerified(): Boolean {
        return Firebase.auth.currentUser!!.isEmailVerified
    }

    override fun isAuthProvider(authProvider: String): Boolean {
        return Firebase.auth.currentUser!!.providerId == authProvider
    }

    override fun getCurrentUser(): String {
        return Firebase.auth.currentUser!!.email!!
    }

    override suspend fun sendVerificationLink() {
        Firebase.auth.currentUser!!.sendEmailVerification(actionCodeSettings).await()
    }

    override suspend fun refreshCurrentUser() {
        Firebase.auth.currentUser!!.reload().await()
    }
}
