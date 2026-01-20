package com.example.homehealth.data.models.chat

data class Message(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val recipientId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "text"
)