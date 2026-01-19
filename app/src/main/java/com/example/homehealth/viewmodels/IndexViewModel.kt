package com.example.homehealth.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.User
import com.example.homehealth.data.repository.UserRepository
import kotlinx.coroutines.launch

class IndexViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _caretakers = MutableLiveData<List<User>>()
    val caretakers: LiveData<List<User>> = _caretakers

    fun fetchUsersByRole(){
        viewModelScope.launch {
            val fetchedUsers = userRepository.getUserByRole("caretaker")
            _caretakers.postValue(fetchedUsers)
        }
    }
}