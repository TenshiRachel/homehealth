package com.example.homehealth.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.chat.Chat
import com.example.homehealth.data.models.chat.Message
import com.example.homehealth.data.repository.ChatRepository
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {
    private val chatRepository: ChatRepository = ChatRepository()

    private val _chat = MutableLiveData<Chat>()
    val chat: LiveData<Chat> = _chat

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    fun fetchChat(chatId: String){
        viewModelScope.launch {
            val fetchedChat = chatRepository.getChatById(chatId)
            _chat.postValue(fetchedChat)
        }
    }

    fun fetchMessages(chatId: String){
        viewModelScope.launch {
            val fetchedMessages = chatRepository.getMessagesByChat(chatId)
            _messages.postValue(fetchedMessages)
        }
    }

     fun sendMessage(chatId: String, senderId: String, recipientId: String, text: String){
         viewModelScope.launch {
             val message = Message(
                 chatId = chatId,
                 senderId = senderId,
                 recipientId = recipientId,
                 text = text
             )
             chatRepository.sendMessage(chatId, message)
         }
    }
}