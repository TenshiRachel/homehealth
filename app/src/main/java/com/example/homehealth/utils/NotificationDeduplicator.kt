package com.example.homehealth.utils

object NotificationDeduplicator {
    private val lastMessageTimeStamp = mutableMapOf<String, Long>()
    private val notifiedAppointments = mutableMapOf<String, String>()

    fun shouldNotifyForChat(chatId: String, newestTimeStamp: Long): Boolean {
        val last = lastMessageTimeStamp[chatId]

        return if (last == null || newestTimeStamp > last){
            lastMessageTimeStamp[chatId] = newestTimeStamp
            true
        } else {
            false
        }
    }

    fun shouldNotifyForAppointment(appointmentId: String, status: String): Boolean {
        val previousStatus = notifiedAppointments[appointmentId]
        return if (previousStatus == status) {
            false
        } else {
            notifiedAppointments[appointmentId] = status
            true
        }
    }
}