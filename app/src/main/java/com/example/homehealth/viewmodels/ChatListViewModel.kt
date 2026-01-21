package com.example.homehealth.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.User
import com.example.homehealth.data.models.chat.Chat
import com.example.homehealth.data.repository.ChatRepository
import com.example.homehealth.data.repository.UserRepository
import kotlinx.coroutines.launch

class ChatListViewModel: ViewModel() {
    private val chatRepository: ChatRepository = ChatRepository()
    private val userRepository: UserRepository = UserRepository()

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser

    private val _chats = MutableLiveData<List<Chat>>()
    val chats: LiveData<List<Chat>> = _chats

    fun fetchCurrentUser(userId: String){
        viewModelScope.launch {
            val fetchedUser = userRepository.getUserById(userId)
            _currentUser.postValue(fetchedUser)
        }
    }

    fun fetchUserChats(userId: String) {
        viewModelScope.launch {
            val fetchedChats = chatRepository.getUserChats(userId)
            _chats.postValue(fetchedChats)
        }
    }
}