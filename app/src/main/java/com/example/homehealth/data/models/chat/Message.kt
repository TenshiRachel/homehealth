package com.example.homehealth.data.models.chat

import com.example.homehealth.data.enums.MessageType

data class MessagePayload(
    val text: String? = null,

    val longitude: Double? = null,
    val latitude: Double? = null,

    val imageBase64: String? = null,
    val imageUrl: String? = null
)

data class Message(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val recipientId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null,

    val type: MessageType = MessageType.TEXT,
    val payload: MessagePayload = MessagePayload()
)