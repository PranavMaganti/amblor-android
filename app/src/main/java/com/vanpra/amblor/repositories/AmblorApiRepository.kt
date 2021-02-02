package com.vanpra.amblor.repositories

import android.content.SharedPreferences
import com.vanpra.amblor.interfaces.AmblorApi
import com.vanpra.amblor.interfaces.NewUser
import com.vanpra.amblor.interfaces.ScrobbleData
import com.vanpra.amblor.interfaces.ScrobbleQuery
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class AmblorApiRepository(private val preferences: SharedPreferences) : AmblorApi {
    private val baseUrl = "https://amblor.herokuapp.com"
    // private val baseUrl = "http://192.168.0.29:8080"

    private val client by lazy {
        HttpClient {
            expectSuccess = false

            defaultRequest {
                contentType(ContentType.Application.Json)
            }

            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }

    private fun HttpRequestBuilder.bearerToken(token: String) =
        header("Authorization", "Bearer $token")

    override suspend fun isUserRegistered(idToken: String): Boolean {
        val res = client.head<HttpResponse>(urlString = "$baseUrl/api/v1/users") {
            bearerToken(idToken)
        }
        return isRequestSuccessful(res)
    }

    override suspend fun signUpUser(username: String, idToken: String): Boolean {
        val res = client.post<HttpResponse>(urlString = "$baseUrl/api/v1/users") {
            bearerToken(idToken)
            body = NewUser(username)
        }
        return isRequestSuccessful(res)
    }

    override suspend fun getAllScrobbles(idToken: String): List<ScrobbleData> {
        val res = client.get<HttpResponse>(urlString = "$baseUrl/api/v1/scrobble") {
            bearerToken(idToken)
            body = preferences.getInt("lastSyncTime", 0)
        }

        if (!isRequestSuccessful(res)) {
            return listOf()
        }

        val scrobbles = res.receive<List<ScrobbleData>>()

        if (scrobbles.isNotEmpty()) {
            val latestScrobbleTime = scrobbles.maxOf { it.time }
            preferences.edit().putInt("lastSyncTime", latestScrobbleTime).apply()
        }

        return scrobbles
    }

    override suspend fun addScrobble(scrobbleQuery: ScrobbleQuery, idToken: String): ScrobbleData? {
        val res = client.post<HttpResponse>(urlString = "$baseUrl/api/v1/scrobble") {
            bearerToken(idToken)
            body = scrobbleQuery
        }

        if (!isRequestSuccessful(res)) {
            return null
        }

        return res.receive()
    }

    private fun isRequestSuccessful(res: HttpResponse): Boolean {
        when (res.status) {
            HttpStatusCode.OK -> {
                return true
            }
            HttpStatusCode.Unauthorized -> {
                println("FIREBASE TOKEN ERROR")
            }
            else -> {
                println(res.status)
            }
        }

        return false
    }
}
