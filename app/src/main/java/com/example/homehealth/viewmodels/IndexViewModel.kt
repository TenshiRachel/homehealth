package com.example.homehealth.viewmodels

import androidx.compose.runtime.mutableStateOf
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

    // for populating screen with available caretakers
    fun fetchCaretakersByRole(){
        viewModelScope.launch {
            val fetchedCaretakers = userRepository.getUserByRole("caretaker")
            _caretakers.postValue(fetchedCaretakers)
        }
    }

    fun getCurrentUser(userId: String) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            currentUser.value = user
        }
    }

    var currentUser = mutableStateOf<User?>(null)
}