package com.example.homehealth.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.User
import com.example.homehealth.data.repository.UserRepository
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val _currentAdmin = mutableStateOf<User?>(null)
    val currentAdmin: State<User?> = _currentAdmin

    fun setAdminSession(admin: User) {
        _currentAdmin.value = admin
    }

    fun isAdmin(): Boolean =
        _currentAdmin.value?.role == "admin"

    private val _users = mutableStateOf<List<User>>(emptyList())
    val users: State<List<User>> = _users

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun loadAllUsers() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                _users.value = UserRepository().getAllUsers()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load users: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deactivateUser(userId: String) {
        // admin-only action
    }
}