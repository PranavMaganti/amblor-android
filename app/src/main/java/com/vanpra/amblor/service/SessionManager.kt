package com.vanpra.amblor.service

import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import com.vanpra.amblor.repositories.NotificationRepository
import com.vanpra.amblor.repositories.NotificationSong
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.Instant

@ExperimentalCoroutinesApi
class SessionManager(private val notificationRepository: NotificationRepository) :
    MediaSessionManager.OnActiveSessionsChangedListener {
    private val activeControllers =
        mutableMapOf<MediaSession.Token, Pair<MediaController, MediaCallback>>()
    private val playerPackages = listOf("com.google.android.apps.youtube.music")

    override fun onActiveSessionsChanged(controllers: MutableList<MediaController>?) {
        if (controllers == null || controllers.isEmpty()) {
            notificationRepository.playingSong.value = NotificationSong.EmptySong
            return
        }

        controllers.filter {
            it.sessionToken !in activeControllers.keys &&
                it.packageName in playerPackages
        }.forEach { controller ->
            val callback = MediaCallback(controller, notificationRepository)
            controller.registerCallback(callback)
            activeControllers[controller.sessionToken] = controller to callback
        }

        val controllerTokens = controllers.map { it.sessionToken }
        activeControllers.filter { it.key !in controllerTokens }.map {
            it.value.first.unregisterCallback(it.value.second)
            activeControllers.remove(it.key)
        }
    }
}

@ExperimentalCoroutinesApi
class MediaCallback(
    private val controller: MediaController,
    private val notifRepo: NotificationRepository
) : MediaController.Callback() {

    override fun onMetadataChanged(metadata: MediaMetadata?) {
        super.onMetadataChanged(metadata)

        println("METADATA CHANGE")

        val currentSong = notifRepo.playingSong.value
        // TODO Take into account song pauses and user definition of scrobble (currently 90%)
        val validSong = !currentSong.scrobbled && currentSong != NotificationSong.EmptySong
        if (validSong && currentSong.playbackTime > currentSong.duration * 0.1) {
            println("SCROBBLING")

            // CoroutineScope(Dispatchers.IO).launch {
            //     val query = ScrobbleQuery(
            //         currentSong.title,
            //         currentSong.artist,
            //         Instant.now().epochSecond.toInt()
            //     )
            //     val tokenId = notificationRepository.auth.getToken()
            //     notificationRepository.amblorApi.addScrobble(query, tokenId)?.let {
            //         notificationRepository.scrobbleDao.insertAllScrobbles(listOf(it))
            //     }
            // }
            currentSong.scrobbled = true
        }

        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)?.trim() ?: ""
        val albumArtist =
            metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)?.trim() ?: ""
        val artist =
            metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)?.trim() ?: albumArtist
        val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION)?.div(1000) ?: -1

        if (currentSong.title != title && currentSong.artist != artist) {
            notifRepo.playingSong.value =
                NotificationSong(title, artist, duration, 0, Instant.now().epochSecond)
            // showSongNotification()
        }
    }

    override fun onPlaybackStateChanged(state: PlaybackState?) {
        super.onPlaybackStateChanged(state)
        println("Playback state: " + state?.state.toString())
        println("Playback time: " + notifRepo.playingSong.value.playbackTime)

        val currentUnixTime = Instant.now().epochSecond

        if (state?.state == PlaybackState.STATE_PAUSED) {
            notifRepo.playingSong.value.playbackTime += currentUnixTime - notifRepo.playingSong.value.lastPlayStart
        } else if (state?.state == PlaybackState.STATE_PLAYING) {
            notifRepo.playingSong.value.lastPlayStart = currentUnixTime
        }

        if (notifRepo.playingSong.value != NotificationSong.EmptySong) {
            notifRepo.playingSong.value.lastState = state?.state ?: 0
        }
    }
}
