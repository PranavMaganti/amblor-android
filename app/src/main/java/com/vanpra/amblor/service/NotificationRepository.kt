package com.vanpra.amblor.service

import android.content.Context
import android.media.session.PlaybackState
import com.vanpra.amblor.data.AmblorDatabase
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

class NotificationRepository(context: Context) {
    val playingSong = MutableStateFlow(NotificationSong.EmptySong)
    val scrobbleDao = AmblorDatabase.getDatabase(context.applicationContext).scrobbleDao()
    lateinit var amblorApi: AmblorApi

    companion object {
        private var instance: NotificationRepository? = null
        fun getInstance(context: Context): NotificationRepository {
            if (instance == null) {
                instance = NotificationRepository(context)
            }
            return instance!!
        }
    }
}