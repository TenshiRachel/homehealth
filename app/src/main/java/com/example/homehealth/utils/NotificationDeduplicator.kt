package com.example.homehealth.utils

object NotificationDeduplicator {
    private val lastMessageTimeStamp = mutableMapOf<String, Long>()

    fun shouldNotifyForChat(chatId: String, newestTimeStamp: Long): Boolean {
        val last = lastMessageTimeStamp[chatId]

        return if (last == null || newestTimeStamp > last){
            lastMessageTimeStamp[chatId] = newestTimeStamp
            true
        } else {
            false
        }
    }
}