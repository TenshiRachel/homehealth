package com.example.homehealth.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.CaretakerDetails
import com.example.homehealth.data.models.User
import com.example.homehealth.data.repository.UserRepository
import com.example.homehealth.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()
//    private val caretakerDetailsRepository = CaretakerDetailsRepository()
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
                _users.value = userRepository.getAllUsers()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load users: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createCaretakerAccount(
        email: String,
        password: String,
        name: String,
        details: CaretakerDetails,
        onResult: (Boolean, String?) -> Unit
    ){
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // Trim inputs
            val cleanEmail = email.trim()
            val cleanName = name.trim()
            val cleanPassword = password.trim()

            try {
                // 1. Create Firebase Auth account
                authRepository.register(cleanEmail, cleanPassword).onSuccess { uid ->

                    // 2. Create User document
                    val user = User(
                        uid = uid,
                        email = cleanEmail,
                        name = cleanName,
                        role = "caretaker",
                        caretakerDetails = details.copy(uid = uid)
                    )
                    val success = userRepository.createUser(user)

                    if (!success) {
                        onResult(false, "Failed to create user profile")
                        return@launch
                    }

                    onResult(true, "Caretaker account created successfully")
                    loadAllUsers()
                }.onFailure { exception ->
                    when (exception) {
                        is FirebaseAuthUserCollisionException ->
                            onResult(false, "Email already registered")

                        else ->
                            onResult(false, "Auth failed: ${exception.localizedMessage}")
                    }
                }
            } catch (e: Exception) {
                onResult(false, "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deactivateUser(userId: String) {
        // admin-only action
    }
}