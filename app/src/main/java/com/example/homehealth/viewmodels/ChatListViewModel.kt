package com.example.homehealth.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.chat.Chat
import com.example.homehealth.data.repository.ChatRepository
import kotlinx.coroutines.launch

class ChatListViewModel: ViewModel() {
    private val chatRepository: ChatRepository = ChatRepository()

    private val _chats = MutableLiveData<List<Chat>>()
    val chats: LiveData<List<Chat>> = _chats

    fun fetchUserChats(userId: String) {
        viewModelScope.launch {
            val fetchedChats = chatRepository.getUserChats(userId)
            _chats.postValue(fetchedChats)
        }
    }
}