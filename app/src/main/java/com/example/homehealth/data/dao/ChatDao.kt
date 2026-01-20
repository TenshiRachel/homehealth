package com.example.homehealth.data.dao

import com.example.homehealth.data.models.chat.Chat
import com.example.homehealth.data.models.chat.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ChatDao {
    private val db = FirebaseFirestore.getInstance()

    suspend fun createOrGetChat(
        user1Id: String,
        user2Id: String
    ): String {
        val query = db.collection("chats")
            .whereArrayContains("members", user1Id)
            .get().await()

        val existing = query.documents.firstOrNull {
            val members = it.get("members") as List<*>
            members.contains(user2Id)
        }

        if (existing != null){
            return existing.id
        }

        val newChat = db.collection("chats").document()
        val chat = Chat(
            id = newChat.id,
            members = listOf(user1Id, user2Id),
            lastMessageTime = System.currentTimeMillis()
        )

        newChat.set(chat).await()
        return newChat.id
    }

    suspend fun sendMessage(chatId: String, message: Message){
        val messageRef = db.collection("chats")
            .document(chatId)
            .collection("messages")
            .document()

        val messageWithId = message.copy(id = messageRef.id)

        messageRef.set(messageWithId).await()

        db.collection("chats")
            .document(chatId)
            .update(
                mapOf(
                    "lastMessage" to message.text,
                    "lastMessageTime" to message.timestamp
                )
            ).await()
    }

    suspend fun getUserChats(userId: String): List<Chat> {
        val snapshot = db.collection("chats")
            .whereArrayContains("members", userId)
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.toObjects(Chat::class.java)
    }
}