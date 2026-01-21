package com.example.homehealth.data.models.chat

data class Chat(
    val id: String = "",
    val memberIds: List<String> = emptyList(),
    val members: List<ChatUser> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L
)