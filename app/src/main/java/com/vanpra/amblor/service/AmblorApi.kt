package com.vanpra.amblor.service

import android.content.SharedPreferences
import androidx.core.content.edit
import com.vanpra.amblor.models.*
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AmblorApi(private val settings: SharedPreferences) {
    companion object {
        private const val baseUrl = "https://amblor.vanpra.me/"

        private var instance: AmblorApi? = null
        fun initalise(sharedPreferences: SharedPreferences) {
            if (instance == null) {
                instance = AmblorApi(sharedPreferences)
            }
        }

        fun getInstance(): AmblorApi {
            if (instance == null) {
                throw IllegalAccessException("AmblorApiRepository must be initialised before access")
            }
            return instance!!
        }
    }

    private val client by lazy {
        HttpClient {
            expectSuccess = false
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }

    suspend fun addUser(user: NewUser): HttpResponse = client.post {
        url {
            takeFrom(baseUrl)
            encodedPath = "api/v1/user/add"
        }

        contentType(ContentType.Application.Json)
        body = user
    }

    private suspend fun checkTokenExpire() {
        if (settings.getInt("expires_at", 0) < Clock.System.now().epochSeconds) {
            refreshToken()
        }
    }

    suspend fun getToken(idToken: String) {
        if (settings.getString("refresh_token", "") != "") {
            checkTokenExpire()
            return
        }

        val token = client.post<Token> {
            url {
                takeFrom(baseUrl)
                encodedPath = "api/v1/token"
            }
            body = TextContent(idToken, ContentType.Text.Plain)
        }

        settings.edit(commit = true) {
            putString("access_token", token.access_token)
            putString("refresh_token", token.refresh_token)
            putInt("expires_at", token.expires_at)
            putString("token_type", token.token_type)
        }
    }

    private suspend fun refreshToken() {
        val refreshToken = settings.getString("refresh_token", "")
        val refreshedToken = client.post<RefreshedToken> {
            url {
                takeFrom(baseUrl)
                encodedPath = "api/v1/refresh"
            }
            contentType(ContentType.Application.Json)
            body = buildJsonObject {
                put("refresh_token", refreshToken)
            }
        }

        settings.edit(commit = true) {
            putString("access_token", refreshedToken.access_token)
            putInt("expires_at", refreshedToken.expires_at)
        }
    }

    suspend fun isNewUser(tokenId: String): Boolean = client.post {
        url {
            takeFrom(baseUrl)
            encodedPath = "api/v1/user"
        }
        body = TextContent(tokenId, ContentType.Text.Plain)
    }

    suspend fun getUserScrobbles(startTime: Int = 0) = client.get<List<ScrobbleData>> {
        checkTokenExpire()
        url {
            takeFrom(baseUrl)
            encodedPath = "api/v1/user/scrobble?start_time=$startTime"
        }
        header("Authorization", "Bearer " + settings.getString("access_token", ""))
    }

    suspend fun scrobble(scrobble: ScrobbleQuery) = client.post<ScrobbleData> {
        checkTokenExpire()
        url {
            takeFrom(baseUrl)
            encodedPath = "api/v1/user/scrobble"
        }
        contentType(ContentType.Application.Json)
        body = scrobble
        header("Authorization", "Bearer " + settings.getString("access_token", ""))
    }
}
