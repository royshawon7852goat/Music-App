package com.pulse.music

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.pulse.music.data.repository.MusicRepository
import com.pulse.music.playback.MusicPlayerManager
import com.pulse.music.playback.MusicService

class PulseApplication : Application() {

    lateinit var musicRepository: MusicRepository
        private set

    lateinit var musicPlayerManager: MusicPlayerManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        musicRepository = MusicRepository(this)
        musicPlayerManager = MusicPlayerManager.getInstance(this)

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MusicService.NOTIFICATION_CHANNEL_ID,
                "Playback Controls",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Pulse music playback controls and notifications"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        lateinit var instance: PulseApplication
            private set
    }
}
