package ru.gordinmitya.dnnbenchmark.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import ru.gordinmitya.dnnbenchmark.R


object NotificationHelper {
    private const val CHANNEL_ID = "FOREGROUND_WORKER_CHANNEL"

    fun createNotification(context: Context, body: String): Notification {
        val title = context.getString(R.string.notification_title)
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_foreground_service)
            .setSound(null)
            .build()
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            serviceChannel.setSound(null, null)
            serviceChannel.vibrationPattern = null
            val manager =
                context.getSystemService<NotificationManager>(NotificationManager::class.java)!!
            manager.createNotificationChannel(serviceChannel)
        }
    }
}