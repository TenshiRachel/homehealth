package com.example.homehealth.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.User
import com.example.homehealth.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel: ViewModel() {
    private val userRepository = UserRepository()
    val auth = FirebaseAuth.getInstance()

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

    private suspend fun registerFirebase(email: String, password: String): Result<String> {
        return try {
            Log.d("FirebaseAuth", "Attempting to register: $email")
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid
            Log.d("FirebaseAuth", "Registration successful. UID: $userId")
            if (userId != null) {
                Result.success(userId)
            } else {
                Log.e("FirebaseAuth", "User ID is null after registration")
                Result.failure(Exception("User ID is null"))
            }
        } catch(e: FirebaseAuthUserCollisionException) {
            Log.e("FirebaseAuth", "Email already in use", e)
            Result.failure(e)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e("FirebaseAuth", "Invalid credentials", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Registration failed with exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun loginFirebase(email: String, password: String, onResult: (Boolean, String?, User?) -> Unit) {
        viewModelScope.launch {
            try {
                // Firebase login
                val authResult = auth.signInWithEmailAndPassword(email, password).await()

                // Check if the login was successful
                val firebaseUid = authResult.user?.uid

                if (firebaseUid != null) {
                    // Fetch the user from Firestore by email
                    val user = userRepository.getUserByEmail(email)

                    if (user != null) {
                        _currentUser.value = user
                        onResult(true, user.uid, user)
                    } else {
                        // If no user found, return failure
                        onResult(false, "User data not found.", null)
                    }
                } else {
                    // If Firebase UID is null
                    onResult(false, "Login failed. Please try again.", null)
                }
            } catch (e: FirebaseAuthInvalidUserException){
                Log.e("FirebaseAuth", "Login failed", e)
                onResult(false, "No user found with this email.", null)
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                // Handle any exceptions related to Firebase Authentication
                Log.e("FirebaseAuth", "Login failed", e)
                onResult(false, "Invalid credentials. Please try again.", null)
            } catch (e: Exception) {
                // Catch any other exceptions
                Log.e("FirebaseAuth", "Unexpected error", e)
                onResult(false, "Unexpected error occurred.", null)
            }
        }
    }

    fun register(name: String, email: String, password: String, confirm: String, onResult: (Boolean, String?) -> Unit) {
        val cleanPassword = password.trim()
        val cleanConfirm = confirm.trim()

        val passwordValidationError = validatePassword(cleanPassword, cleanConfirm)

        if (passwordValidationError != null) {
            // If the password is invalid, return the error message
            onResult(false, passwordValidationError)
            return
        }

        viewModelScope.launch {
            val firebaseResult = registerFirebase(email.trim(), cleanPassword)
            if (firebaseResult.isSuccess) {
                val userId = firebaseResult.getOrNull()!!
                Log.d("Registration", "Firebase Auth successful. UserId: $userId")

                // Create new user and store to firebase if registered successfully
                val user = User(uid = userId, name = name, email = email, role = "public")
                Log.d("Registration", "Created User object: $user")

                val dbSuccess = userRepository.createUser(user)
                Log.d("Registration", "Firestore write result: $dbSuccess")

                if (dbSuccess) {
                    Log.d("User Registered", "Success")
                    onResult(true, "Registration successful! Please log in")
                } else {
                    Log.d("User Registration", "Failed to save user in Firestore")
                    onResult(false, "Failed to save user in Firebase")
                }
            } else {
                val exception = firebaseResult.exceptionOrNull()
                Log.d("Firebase Registration", "Failed", exception)
                // Handling Firebase errors more granularly
                when (exception) {
                    // If duplicate email
                    is FirebaseAuthUserCollisionException -> {
                        onResult(false, "The email is already registered.")
                    }
                    // If invalid email format
                    is FirebaseAuthInvalidCredentialsException -> {
                        onResult(false, "The email format is invalid.")
                    }
                    else -> {
                        // Other exceptions
                        onResult(false, "Could not register with Firebase: ${exception?.localizedMessage}")
                    }
                }
            }
        }
    }

    fun logout(){
        _currentUser.value = null
        auth.signOut()
    }
}