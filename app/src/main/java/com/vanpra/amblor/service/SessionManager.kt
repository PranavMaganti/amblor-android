package com.vanpra.amblor.service

import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import com.vanpra.amblor.models.ScrobbleQuery
import com.vanpra.amblor.repositories.NotificationRepository
import com.vanpra.amblor.repositories.NotificationSong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.time.Instant

@ExperimentalCoroutinesApi
class SessionManager(private val notificationRepository: NotificationRepository) :
    MediaSessionManager.OnActiveSessionsChangedListener {
    private val activeControllers =
        mutableMapOf<MediaSession.Token, Pair<MediaController, MediaCallback>>()
    private val playerPackages = listOf("com.google.android.apps.youtube.music")

    override fun onActiveSessionsChanged(controllers: MutableList<MediaController>?) {
        if (controllers?.isEmpty() != false) {
            notificationRepository.playingSong.value = NotificationSong.EmptySong
            return
        }

        controllers.filter {
            it.sessionToken !in activeControllers.keys &&
                it.packageName in playerPackages
        }.forEach { controller ->
            val callback = MediaCallback(controller, notificationRepository)
            controller.registerCallback(callback)
            callback.onMetadataChanged(controller.metadata)
            callback.onPlaybackStateChanged(controller.playbackState)
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
    private val notificationRepository: NotificationRepository
) :
    MediaController.Callback() {

    // Increments id on dismiss so that the notification is re-shown when song is changed
    // private val broadcastReceiver = object : BroadcastReceiver() {
    //     override fun onReceive(p0: Context?, p1: Intent?) {
    //         notificationId++
    //     }
    // }

    override fun onMetadataChanged(metadata: MediaMetadata?) {
        super.onMetadataChanged(metadata)

        val currentSong = notificationRepository.playingSong.value
        // TODO Take into account song pauses and user definition of scrobble (currently 90%)
        val minScrobbleTime = (currentSong.startTime + currentSong.duration * 0.1).toLong()
        val validSong = !currentSong.scrobbled && currentSong != NotificationSong.EmptySong
        if (validSong && System.currentTimeMillis() >= minScrobbleTime) {
            CoroutineScope(Dispatchers.IO).launch {
                val query = ScrobbleQuery(
                    currentSong.title,
                    currentSong.artist,
                    Instant.now().epochSecond.toInt()
                )

                // val scrobble = notificationRepository.amblorApi.scrobble(query)
                // notificationRepository.scrobbleDao.insertAllScrobbles(listOf(scrobble))
            }
            currentSong.scrobbled = true
        }

        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)?.trim() ?: ""
        val albumArtist =
            metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)?.trim() ?: ""
        val artist =
            metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)?.trim() ?: albumArtist
        println(albumArtist)
        println(artist)
        val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: -1

        if (currentSong.title != title && currentSong.artist != artist) {
            val time = System.currentTimeMillis()
            notificationRepository.playingSong.value =
                NotificationSong(title, artist, duration, time)
            // showSongNotification()
        }
    }

    override fun onPlaybackStateChanged(state: PlaybackState?) {
        super.onPlaybackStateChanged(state)

        // when (state?.state) {
        //     PlaybackState.STATE_PLAYING -> {
        //         showSongNotification()
        //     }
        //
        //     PlaybackState.STATE_PAUSED -> {
        //         nm.cancel(notificationId)
        //     }
        // }
        val currentSong = notificationRepository.playingSong.value
        val playing = PlaybackState.STATE_PLAYING
        if (currentSong.lastState == playing && state?.state == playing) {
            onMetadataChanged(controller.metadata)
        }

        if (currentSong != NotificationSong.EmptySong) {
            notificationRepository.playingSong.value =
                currentSong.copy(lastState = state?.state ?: 0)
        }
    }

    // private fun showSongNotification() {
    //     val notification: Notification = NotificationCompat.Builder(context, CHANNEL_ID)
    //         .setContentTitle(currentSong.title)
    //         .setContentText(currentSong.artist)
    //         .setSmallIcon(R.drawable.ic_launcher_foreground)
    //         .build()
    //
    //     val intent = Intent(context, broadcastReceiver::class.java)
    //     val deleteIntent =
    //         PendingIntent.getBroadcast(context.applicationContext, 0, intent, 0)
    //
    //     notification.deleteIntent = deleteIntent
    //     nm.notify(notificationId, notification)
    // }
}
