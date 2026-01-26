package com.example.homehealth.data.models.chat

enum class MessageType {
    TEXT,
    LOCATION,
    IMAGE
}

data class MessagePayload(
    val text: String? = null,

    val longitude: Double? = null,
    val latitude: Double? = null,
)

data class Message(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val recipientId: String = "",
    val timestamp: Long = System.currentTimeMillis(),

    val type: MessageType = MessageType.TEXT,
    val payload: MessagePayload = MessagePayload()
)