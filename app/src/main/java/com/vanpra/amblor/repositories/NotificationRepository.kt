package com.vanpra.amblor.repositories

import android.media.session.PlaybackState
import com.vanpra.amblor.data.AmblorDatabase
import com.vanpra.amblor.interfaces.AmblorApi
import com.vanpra.amblor.interfaces.AuthenticationApi
import kotlinx.coroutines.flow.MutableStateFlow

data class NotificationSong(
    val title: String,
    val artist: String,
    val duration: Long,
    var playbackTime: Long = 0,
    var lastPlayStart: Long = -1,
    var lastState: Int = PlaybackState.STATE_NONE,
    var scrobbled: Boolean = false
) {
    companion object {
        val EmptySong = NotificationSong("", "", -1)
    }
}

class NotificationRepository(
    val amblorApi: AmblorApi,
    val auth: AuthenticationApi,
    amblorDatabase: AmblorDatabase
) {
    var playingSong = MutableStateFlow(NotificationSong.EmptySong)
    val scrobbleDao = amblorDatabase.scrobbleDao()
}
