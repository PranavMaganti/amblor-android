package com.vanpra.amblor.repositories

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.vanpra.amblor.models.NewUser
import com.vanpra.amblor.models.RefreshedToken
import com.vanpra.amblor.models.ScrobbleData
import com.vanpra.amblor.models.ScrobbleQuery
import com.vanpra.amblor.models.Token
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import kotlinx.datetime.Clock

class AmblorApiRepository(context: Context) {
    companion object {
        private const val baseUrl = "https://amblor.vanpra.me/"

        private var instance: AmblorApiRepository? = null

        fun getInstance(context: Context): AmblorApiRepository {
            if (instance == null) {
                instance = AmblorApiRepository(context)
            }
            return instance!!
        }
    }

    private val client by lazy {
        HttpClient {
            expectSuccess = false
        }
    }
    private val settings = PreferenceManager.getDefaultSharedPreferences(context)

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
