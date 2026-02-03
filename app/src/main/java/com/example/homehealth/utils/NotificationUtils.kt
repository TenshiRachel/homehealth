package com.example.homehealth.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.homehealth.R

const val CHAT_CHANNEL_ID = "chat_channel"
const val APPOINTMENT_CHANNEL_ID = "appointment_channel"

fun createNotificationChannels(context: Context){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val chatChannel = NotificationChannel(
            CHAT_CHANNEL_ID,
            "Chat Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val appointmentChannel = NotificationChannel(
            APPOINTMENT_CHANNEL_ID,
            "Appointment Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(chatChannel)
        manager.createNotificationChannel(appointmentChannel)
    }
}

@SuppressLint("MissingPermission")
fun showNotification(
    context: Context,
    channelId: String,
    title: String,
    body: String
){
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(title)
        .setContentText(body)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context)
        .notify(System.currentTimeMillis().toInt(), notification)
}