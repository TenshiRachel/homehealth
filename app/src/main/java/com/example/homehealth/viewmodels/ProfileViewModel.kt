package com.example.homehealth.viewmodels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.User
import com.example.homehealth.data.models.EditProfileState
import com.example.homehealth.data.repository.UserRepository
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    // Source of truth for viewing
    private val userRepository = UserRepository()

    // Edit UI state
    private val _editState = MutableStateFlow(EditProfileState())
    val editState: StateFlow<EditProfileState> = _editState
    var profileUser = mutableStateOf<User?>(null)
        private set

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            profileUser.value = user

            // Initialize edit state
            user?.let {
                _editState.value = EditProfileState(
                    name = it.name,
                    bio = it.bio ?: ""
                )
            }
        }
    }

    fun onNameChanged(value: String) {
        _editState.value = _editState.value.copy(name = value)
    }

    fun onBioChanged(value: String) {
        _editState.value = _editState.value.copy(bio = value)
    }

    fun saveProfile() {
        val user = profileUser.value ?: return
        val state = editState.value

        viewModelScope.launch {
            val updated = user.copy(
                name = state.name,
                bio = state.bio
            )
            val success = userRepository.updateUser(updated)
            if (success) {
                profileUser.value = updated
            }
        }
    }

}