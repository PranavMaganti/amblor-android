package com.vanpra.amblor.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.session.MediaSessionManager
import android.os.Build
import android.service.notification.NotificationListenerService
import androidx.core.app.NotificationCompat
import com.vanpra.amblor.R
import com.vanpra.amblor.repositories.NotificationRepository
import com.vanpra.amblor.repositories.NotificationSong
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

const val CHANNEL_ID = "Amblor"
const val NOTIFICATION_ID = 1000

@ExperimentalCoroutinesApi
class AmblorService : NotificationListenerService() {
    lateinit var notificationManager: NotificationManager
    lateinit var sessionManager: SessionManager
    private lateinit var mediaManager: MediaSessionManager

    override fun onCreate() {
        super.onCreate()
        notificationManager =
            this.getSystemService(NotificationManager::class.java) as NotificationManager
        createNotificationChannel(notificationManager)
        val waitingNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Amblor")
            .setContentText("Amblor is waiting for music")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        startForeground(NOTIFICATION_ID, waitingNotification)

        val repository = NotificationRepository.getInstance(this)
        GlobalScope.launch {
            repository.playingSong.collect {
                val songNotification = if (it == NotificationSong.EmptySong) {
                    waitingNotification
                } else {
                    NotificationCompat.Builder(this@AmblorService, CHANNEL_ID)
                        .setContentTitle(it.title)
                        .setContentText(it.artist)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .build()
                }
                notificationManager.notify(NOTIFICATION_ID, songNotification)
            }
        }
    }

    override fun onListenerConnected() {
        mediaManager =
            this.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        sessionManager = SessionManager(this)
        mediaManager.addOnActiveSessionsChangedListener(
            sessionManager,
            ComponentName(this, this::class.java)
        )

        sessionManager.onActiveSessionsChanged(
            mediaManager.getActiveSessions(
                ComponentName(
                    this,
                    this::class.java
                )
            )
        )
        super.onListenerConnected()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val thisComponent = ComponentName(this, AmblorService::class.java)
        val pm = this.packageManager
        pm.setComponentEnabledSetting(
            thisComponent,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        pm.setComponentEnabledSetting(
            thisComponent,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        return Service.START_STICKY
    }

    private fun createNotificationChannel(nm: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Scrobbling",
                NotificationManager.IMPORTANCE_LOW
            )
            nm.createNotificationChannel(serviceChannel)
        }
    }
}
