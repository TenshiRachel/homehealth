package com.example.homehealth.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.User
import com.example.homehealth.data.models.chat.Chat
import com.example.homehealth.data.models.chat.Message
import com.example.homehealth.data.repository.ChatRepository
import com.example.homehealth.data.repository.UserRepository
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {
    private val chatRepository: ChatRepository = ChatRepository()
    private val userRepository: UserRepository = UserRepository()

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser

    private val _chat = MutableLiveData<Chat>()
    val chat: LiveData<Chat> = _chat

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    fun fetchCurrentUser(userId: String){
        viewModelScope.launch {
            val fetchedUser = userRepository.getUserById(userId)
            _currentUser.postValue(fetchedUser)
        }
    }

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