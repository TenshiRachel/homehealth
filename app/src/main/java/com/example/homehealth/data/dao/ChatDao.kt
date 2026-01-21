package com.example.homehealth.data.dao

import com.example.homehealth.data.models.chat.Chat
import com.example.homehealth.data.models.chat.ChatUser
import com.example.homehealth.data.models.chat.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ChatDao {
    private val db = FirebaseFirestore.getInstance()

    suspend fun createOrGetChat(
        user1: ChatUser,
        user2: ChatUser
    ): String {

        val query = db.collection("chats")
            .whereArrayContains("memberIds", user1.uid)
            .get()
            .await()

        val existing = query.documents.firstOrNull { doc ->
            val memberIds = doc.get("memberIds") as? List<*>
            memberIds?.contains(user2.uid) == true
        }

        if (existing != null) {
            return existing.id
        }

        val newChatRef = db.collection("chats").document()

        val chat = Chat(
            id = newChatRef.id,
            members = listOf(user1, user2),
            lastMessageTime = System.currentTimeMillis()
        )

        newChatRef.set(chat).await()
        return newChatRef.id
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