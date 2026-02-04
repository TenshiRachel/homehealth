package com.example.homehealth.viewmodels
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.User
import com.example.homehealth.data.models.EditProfileState
import com.example.homehealth.data.repository.UserRepository
import androidx.compose.runtime.mutableStateOf
import com.example.homehealth.data.repository.StorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
class ProfileViewModel : ViewModel() {

    // Source of truth for viewing
    private val userRepository = UserRepository()
    private val storageRepository = StorageRepository()

    // Profile user state
    var profileUser = mutableStateOf<User?>(null)
        private set

    // Edit UI state
    private val _editState = MutableStateFlow(EditProfileState())
    val editState: StateFlow<EditProfileState> = _editState

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            profileUser.value = user

            // Initialize edit state
            user?.let {
                _editState.value = EditProfileState(
                    name = it.name,
                    bio = it.bio ?: "",
                    profileImageUrl = it.profileImageUrl
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
                bio = state.bio,
                profileImageUrl = state.profileImageUrl
            )
            val success = userRepository.updateUser(updated)
            if (success) {
                profileUser.value = updated
            }
        }
    }

    fun uploadProfileImage(userId: String, uri: Uri) {
        viewModelScope.launch {
            _editState.value = _editState.value.copy(isSaving = true)

            val path = "profile_images/$userId.jpg"
            val result = storageRepository.uploadFile(uri, path)

            result.onSuccess { url ->
                _editState.value = _editState.value.copy(
                    profileImageUrl = url,
                    isSaving = false
                )
            }.onFailure {
                _editState.value = _editState.value.copy(
                    isSaving = false,
                    error = it.message
                )
            }
        }
    }

}