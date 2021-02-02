package com.vanpra.amblor.interfaces

import kotlinx.serialization.Serializable

@Serializable
data class NewUser(
    val username: String
)

@Serializable
data class ScrobbleData(
    val time: Int = 0,
    val name: String = "",
    val preview_url: String = "",
    val album_name: String = "",
    val image: String = "",
    val artist_names: String = "",
    val artist_images: String = "",
    val artist_genres: String = ""
)

@Serializable
data class ScrobbleQuery(
    val track_name: String,
    val artist_name: String,
    val time: Int
)


interface AmblorApi {
    suspend fun signUpUser(username: String, idToken: String): Boolean
    suspend fun isUserRegistered(idToken: String): Boolean
    suspend fun getAllScrobbles(idToken: String): List<ScrobbleData>
    suspend fun addScrobble(scrobbleQuery: ScrobbleQuery, idToken: String): ScrobbleData?
}
