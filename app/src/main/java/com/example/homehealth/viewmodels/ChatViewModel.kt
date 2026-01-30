package com.example.homehealth.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.chat.Chat
import com.example.homehealth.data.models.chat.Message
import com.example.homehealth.data.models.chat.MessagePayload
import com.example.homehealth.data.enums.MessageType
import com.example.homehealth.data.repository.ChatRepository
import com.example.homehealth.utils.LocationProvider
import com.example.homehealth.utils.compressImageToBase64
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val chatRepository: ChatRepository = ChatRepository()
    private val locationProvider: LocationProvider = LocationProvider(application)

    private val _chat = MutableLiveData<Chat>()
    val chat: LiveData<Chat> = _chat

    fun fetchChat(chatId: String){
        viewModelScope.launch {
            val fetchedChat = chatRepository.getChatById(chatId)
            _chat.postValue(fetchedChat)
        }
    }

    fun fetchMessages(chatId: String): Flow<List<Message>> {
        return chatRepository.getMessagesByChat(chatId)
    }

     fun sendMessage(chatId: String, senderId: String, recipientId: String, text: String){
         viewModelScope.launch {
             val message = Message(
                 chatId = chatId,
                 senderId = senderId,
                 recipientId = recipientId,
                 type = MessageType.TEXT,
                 payload = MessagePayload(text = text)
             )
             chatRepository.sendMessage(chatId, message)
         }
    }

    fun sendLocation(chatId: String, senderId: String, recipientId: String){
        viewModelScope.launch {
            // val location = locationProvider.getLastLocation() ?: return@launch
            val location = locationProvider.getCurrentLocation()
            if (location == null) {
                Log.e("LocationProvider", "Location is null")
                return@launch
            }

            val message = Message(
                chatId = chatId,
                senderId = senderId,
                recipientId = recipientId,
                expiresAt = System.currentTimeMillis() + 5 * 60 * 1000,
                type = MessageType.LOCATION,
                payload = MessagePayload(longitude = location.longitude, latitude = location.latitude)
            )

            chatRepository.sendMessage(chatId, message)
        }
    }

    fun sendImage(chatId: String, senderId: String, recipientId: String, imageUri: Uri){
        viewModelScope.launch {
            val appContext = getApplication<Application>()

            val base64Image = compressImageToBase64(
                context = appContext,
                uri = imageUri
            )

            val message = Message(
                chatId = chatId,
                senderId = senderId,
                recipientId = recipientId,
                type = MessageType.IMAGE,
                payload = MessagePayload(
                    imageBase64 = base64Image
                )
            )

            chatRepository.sendMessage(chatId, message)
        }
    }
}