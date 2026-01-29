package com.example.homehealth.viewmodels

import androidx.lifecycle.ViewModel
import com.example.homehealth.data.repository.UserRepository
import com.example.homehealth.data.repository.SkillRepository
import com.example.homehealth.data.repository.CertificationRepository
import com.example.homehealth.data.models.CaretakerProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.enums.AvailabilityType
import com.example.homehealth.data.enums.CaretakerType

class CaretakerViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val skillRepository = SkillRepository()
    private val certificationRepository = CertificationRepository()

    private val _caretakerProfile = MutableStateFlow<CaretakerProfile?>(null)
    val caretakerProfile: StateFlow<CaretakerProfile?> = _caretakerProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadCaretakerProfile(caretakerUid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Fetch user info
                val user = userRepository.getUserById(caretakerUid)
                val details = user?.caretakerDetails

                if (user == null || details == null) {
                    _error.value = "Caretaker profile incomplete"
                    return@launch
                }

                // Resolve certifications (admin assigned)
                val certifications = details.certificationIds.mapNotNull { certId ->
                    certificationRepository.getCertificationById(certId)
                }

                // Resolve skills (caretaker assigned)
                val skills = details.skillIds.mapNotNull { skillId ->
                    skillRepository.getSkillById(skillId)
                }

                // Combine into CaretakerProfile
                _caretakerProfile.value = CaretakerProfile(
                    uid = user.uid,
                    name = user.name,
                    email = user.email,
                    bio = user.bio ?: "",
                    gender = details.gender,
                    age = details.age,
                    yearsOfExperience = details.yearsOfExperience,
                    caretakerType = details.caretakerType,
                    availabilityType = details.availabilityType,
                    nightCare = details.nightCare,
                    skills = skills,
                    certifications = certifications
                    )
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCaretakerProfile(
        bio: String,
        caretakerType: CaretakerType,
        availabilityType: AvailabilityType,
        nightCare: Boolean,
        skillIds: List<String>,
        onResult: (Boolean, String?) -> Unit
    ) {
        val currentProfile = _caretakerProfile.value

        if (currentProfile == null) {
            onResult(false, "Profile not loaded")
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null

            try {
                // Fetch latest user to avoid overwriting admin fields
                val user = userRepository.getUserById(currentProfile.uid)
                    ?: throw Exception("User not found")

                val existingDetails = user.caretakerDetails
                    ?: throw Exception("Caretaker details missing")

                // Merge ONLY caretaker-owned fields
                val updatedDetails = existingDetails.copy(
                    caretakerType = caretakerType,
                    availabilityType = availabilityType,
                    nightCare = nightCare,
                    skillIds = skillIds
                )

                val updatedUser = user.copy(
                    bio = bio,
                    caretakerDetails = updatedDetails
                )

                val success = userRepository.updateUser(updatedUser)

                if (success) {
                    // Refresh local profile after save
                    loadCaretakerProfile(user.uid)
                    onResult(true, null)
                } else {
                    onResult(false, "Failed to update profile")
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "Unknown error")
            } finally {
                _isSaving.value = false
            }
        }
    }

}