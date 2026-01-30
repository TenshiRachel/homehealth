package com.example.homehealth.data.dao

import android.util.Log
import com.example.homehealth.data.models.chat.Chat
import com.example.homehealth.data.models.chat.ChatUser
import com.example.homehealth.data.models.chat.Message
import com.example.homehealth.data.enums.MessageType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatDao {
    private val db = FirebaseFirestore.getInstance()
    companion object {
        private const val CHATS_COLLECTION = "Chats"
        private const val MESSAGES_COLLECTION = "Messages"
    }

    suspend fun createOrGetChat(
        user1: ChatUser,
        user2: ChatUser
    ): String {

        val query = db.collection(CHATS_COLLECTION)
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

        val newChatRef = db.collection(CHATS_COLLECTION).document()

        val chat = Chat(
            id = newChatRef.id,
            memberIds = listOf(user1.uid, user2.uid),
            members = listOf(user1, user2),
            lastMessageTime = System.currentTimeMillis()
        )

        newChatRef.set(chat).await()
        return newChatRef.id
    }

    suspend fun sendMessage(chatId: String, message: Message){
        val messageRef = db.collection(CHATS_COLLECTION)
            .document(chatId)
            .collection(MESSAGES_COLLECTION)
            .document()

        val messageWithId = message.copy(id = messageRef.id)

        messageRef.set(messageWithId).await()

        db.collection(CHATS_COLLECTION)
            .document(chatId)
            .update(
                mapOf(
                    "lastMessage" to when (message.type){
                        MessageType.TEXT -> message.payload.text
                        MessageType.LOCATION -> "Location"
                        MessageType.IMAGE -> "Image"
                    },
                    "lastMessageTime" to message.timestamp
                )
            ).await()
    }

    suspend fun getChatById(chatId: String): Chat? {
        return try {
            val querySnapshot = db.collection(CHATS_COLLECTION)
                .whereEqualTo("id", chatId)
                .get()
                .await()
            querySnapshot.documents.firstOrNull()?.toObject(Chat::class.java)
        } catch (e: Exception){
            Log.d("Chat", "Retrieving chat by id failed", e)
            null
        }
    }

    suspend fun getUserChats(userId: String): List<Chat> {
        val snapshot = db.collection(CHATS_COLLECTION)
            .whereArrayContains("memberIds", userId)
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.toObjects(Chat::class.java)
    }

    fun getMessagesByChat(chatId: String): Flow<List<Message>> = callbackFlow {
        val listener = db.collection(CHATS_COLLECTION)
            .document(chatId)
            .collection(MESSAGES_COLLECTION)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                trySend(messages)
            }

        awaitClose { listener.remove() }
    }
}