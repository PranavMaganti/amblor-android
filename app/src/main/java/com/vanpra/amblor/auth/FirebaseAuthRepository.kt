package com.vanpra.amblor.auth

import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository : AuthenticationApi {
    override suspend fun createUserWithEmail(username: String, password: String) {
        try {
            Firebase.auth.createUserWithEmailAndPassword(username, password).await()
        } catch (e: FirebaseAuthUserCollisionException) {
            throw EmailAlreadyRegistered("Email already used")
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
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun getToken(): String? {
        return Firebase.auth.currentUser!!.getIdToken(true).await().token!!
    }

    override fun signOut() {
        Firebase.auth.signOut()
    }

    override fun isUserSignedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }
}
