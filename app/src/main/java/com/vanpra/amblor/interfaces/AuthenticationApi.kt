package com.vanpra.amblor.interfaces

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import kotlin.jvm.Throws

class UserAlreadyRegisteredException(message: String) : Exception(message)
class InvalidPasswordException(message: String) : Exception(message)

interface AuthenticationApi {
    val googleClient: GoogleSignInClient

    suspend fun createUserWithEmail(username: String, password: String)
    suspend fun deleteCurrentUser()
    @Throws(UserAlreadyRegisteredException::class)
    suspend fun fetchSignInMethodsForEmail(email: String): List<String>
    @Throws(InvalidPasswordException::class)
    suspend fun signInWithEmailAndPassword(email: String, password: String)
    suspend fun getToken(): String
    suspend fun signOut()
    fun isUserSignedIn(): Boolean
    suspend fun signInWithCredential(credential: AuthCredential): Boolean
    fun isUserEmailVerified(): Boolean
    fun isAuthProvider(authProvider: String): Boolean
    fun getCurrentUser(): String
    suspend fun sendVerificationLink()
    suspend fun refreshCurrentUser()
}
