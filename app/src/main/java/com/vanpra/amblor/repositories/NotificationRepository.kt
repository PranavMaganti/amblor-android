package com.vanpra.amblor.repositories

import android.media.session.PlaybackState
import kotlinx.coroutines.flow.MutableStateFlow

data class NotificationSong(
    val title: String,
    val artist: String,
    val duration: Long,
    val startTime: Long,
    var lastState: Int = PlaybackState.STATE_NONE,
    var scrobbled: Boolean = false
) {
    companion object {
        val EmptySong = NotificationSong("", "", -1, 0)
    }
}

class NotificationRepository(amblorApi: AmblorApiRepository) {
    val playingSong = MutableStateFlow(NotificationSong.EmptySong)
    // val scrobbleDao = AmblorDatabase.getDatabase(context.applicationContext).scrobbleDao()
}
