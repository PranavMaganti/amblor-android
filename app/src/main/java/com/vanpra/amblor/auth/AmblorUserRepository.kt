package com.vanpra.amblor.auth

import com.vanpra.amblor.models.NewUser
import com.vanpra.amblor.ui.layouts.auth.EmailSignupModel
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.takeFrom

class AmblorUserRepository: AmblorUserApi {
    private val baseUrl = "http://192.168.0.29:8080"

    private val client by lazy {
        HttpClient {
            expectSuccess = false

            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }

    override suspend fun signupEmailUser(signupModel: EmailSignupModel, idToken: String): Boolean {
        val res = client.post<HttpResponse> {
            url {
                takeFrom(baseUrl)
                encodedPath = "/users"
            }

            contentType(ContentType.Application.Json)
            body = NewUser(signupModel.username.text, idToken)
        }

        when (res.status) {
            HttpStatusCode.OK -> {
                return true
            }
            HttpStatusCode.Conflict -> {
                signupModel.username.showError("Username Taken")
            }
            HttpStatusCode.Unauthorized -> {
                println("FIREBASE TOKEN ERROR")
            }
        }

        return false
    }
}
