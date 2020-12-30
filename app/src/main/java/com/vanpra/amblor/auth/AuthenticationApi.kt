package com.vanpra.amblor.auth

class EmailAlreadyRegistered(message: String) : Exception(message)
class InvalidPassword(message: String) : Exception(message)

interface AuthenticationApi {
    suspend fun createUserWithEmail(username: String, password: String)
    suspend fun deleteCurrentUser()
    suspend fun fetchSignInMethodsForEmail(email: String): List<String>
    suspend fun signInWithEmailAndPassword(email: String, password: String)
    suspend fun getToken(): String?
    fun signOut()
    fun isUserSignedIn(): Boolean
}
