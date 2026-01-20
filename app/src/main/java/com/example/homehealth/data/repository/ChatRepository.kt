package com.example.homehealth.data.repository

import com.example.homehealth.data.dao.ChatDao
import com.example.homehealth.data.models.chat.Chat
import com.example.homehealth.data.models.chat.Message

class ChatRepository (private val chatDao: ChatDao = ChatDao()) {
    suspend fun startChat(user1Id: String, user2Id: String): String {
        return chatDao.createOrGetChat(user1Id, user2Id)
    }

    suspend fun sendMessage(
        chatId: String,
        senderId: String,
        recipientId: String,
        text: String
    ) {
        val message = Message(
            chatId = chatId,
            senderId = senderId,
            recipientId = recipientId,
            text = text
        )
        chatDao.sendMessage(chatId, message)
    }

    suspend fun getUserChats(userId: String): List<Chat>{
        return chatDao.getUserChats(userId)
    }
}