package com.vanpra.amblor.util

import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.AuthProvider
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.auth.HttpAuthHeader

fun Auth.jwt(block: BearerAuth.() -> Unit) {
    with(BearerAuth().apply(block)) {
        providers.add(BearerAuthProvider(token))
    }
}

class BearerAuth {
    lateinit var token: String
}

const val Bearer = "Bearer"

class BearerAuthProvider(
    private val token: String,
    override val sendWithoutRequest: Boolean = false
) : AuthProvider {
    override fun isApplicable(auth: HttpAuthHeader): Boolean {
        if (auth.authScheme != Bearer) return false
        return true
    }

    override suspend fun addRequestHeaders(request: HttpRequestBuilder) {
        request.headers[HttpHeaders.Authorization] = constructBearerAuthValue()
    }

    private fun constructBearerAuthValue(): String {
        return "Bearer $token"
    }
}
