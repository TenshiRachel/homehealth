package com.example.homehealth.data.repository

import com.example.homehealth.data.dao.ChatDao
import com.example.homehealth.data.models.chat.Chat
import com.example.homehealth.data.models.chat.ChatUser
import com.example.homehealth.data.models.chat.Message

class ChatRepository (private val chatDao: ChatDao = ChatDao()) {
    suspend fun startChat(user1Id: ChatUser, user2Id: ChatUser): String {
        return chatDao.createOrGetChat(user1Id, user2Id)
    }

    suspend fun sendMessage(chatId: String, message: Message) {
        chatDao.sendMessage(chatId, message)
    }

    suspend fun getChatById(chatId: String): Chat? {
        return chatDao.getChatById(chatId)
    }

    suspend fun getUserChats(userId: String): List<Chat>{
        return chatDao.getUserChats(userId)
    }

    suspend fun getMessagesByChat(chatId: String): List<Message>{
        return chatDao.getMessagesByChat(chatId)
    }
}