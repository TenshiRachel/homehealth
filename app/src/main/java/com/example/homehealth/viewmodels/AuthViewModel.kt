package com.example.homehealth.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.User
import com.example.homehealth.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.launch
import com.example.homehealth.data.repository.AuthRepository

class AuthViewModel: ViewModel() {
    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()

    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> = _currentUser

    private fun validatePassword(password: String, confirm: String): String? {
        // Check if password is at least 6 characters long
        if (password.length < 6) {
            return "Password must be at least 6 characters long."
        }

        // Check if password is alphanumeric (only letters and numbers)
        val regex = "^[a-zA-Z0-9]+$".toRegex()
        if (!password.matches(regex)) {
            return "Password must be alphanumeric."
        }

        if (!password.any { it.isLetter() }) {
            return "Password must contain at least one letter."
        }

        if (!password.any { it.isDigit() }) {
            return "Password must contain at least one number."
        }

        if (password != confirm){
            return "Passwords do not match."
        }

        // Return null if validation passes
        return null
    }

    fun register(name: String, email: String, password: String, confirm: String, onResult: (Boolean, String?) -> Unit) {
        val cleanPassword = password.trim()
        val cleanConfirm = confirm.trim()
        val cleanEmail = email.trim()
        val cleanName = name.trim()

        val passwordValidator = validatePassword(cleanPassword, cleanConfirm)

        if (passwordValidator != null) {
            // If the password is invalid, return the error message
            onResult(false, passwordValidator)
            return
        }

        viewModelScope.launch {
            // 1. Register to Firebase Auth
            authRepository.register(cleanEmail, cleanPassword).onSuccess { uid ->
                // 2. Create new user and store to firebase if registered successfully
                val user = User(uid = uid, name = cleanName, email = cleanEmail, role = "public")
                Log.d("Registration", "Created User object: $user")

                val isSuccess = userRepository.createUser(user)
                if (isSuccess) {
                    Log.d("User Registered", "Success")
                    onResult(true, "Registration successful! Please log in")
                } else {
                    Log.d("User Registration", "Failed to save user in Firestore")
                    onResult(false, "Failed to save user in Firebase")
                }
            }.onFailure { exception ->
                when (exception) {
                    is FirebaseAuthUserCollisionException ->
                        onResult(false, "Email already registered")
                    else ->
                        onResult(false, exception.localizedMessage)
                }
            }
        }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?, User?) -> Unit) {
        viewModelScope.launch {
            authRepository.login(email, password).onSuccess { uid ->
                val user = userRepository.getUserByEmail(email)
                if (user != null) {
                    _currentUser.value = user
                    onResult(true, null, user)
                } else {
                    onResult(false, "User data not found.", null)
                }
            }.onFailure { exception ->
                when (exception) {
                    is FirebaseAuthInvalidUserException ->
                        onResult(false, "No user found with this email.", null)
                    is FirebaseAuthInvalidCredentialsException ->
                        onResult(false, "Invalid credentials. Please try again.", null)
                    else ->
                        onResult(false, "Unexpected error occurred.", null)
                }
            }
        }
    }

    fun logout(){
        _currentUser.value = null
        authRepository.logout()
    }
}