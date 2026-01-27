package com.example.homehealth.viewmodels

import androidx.lifecycle.ViewModel
import com.example.homehealth.data.repository.UserRepository
import com.example.homehealth.data.repository.CaretakerDetailsRepository
import com.example.homehealth.data.repository.SkillRepository
import com.example.homehealth.data.repository.CertificationRepository
import com.example.homehealth.data.models.CaretakerProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class CaretakerViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val caretakerDetailsRepository = CaretakerDetailsRepository()
    private val skillRepository = SkillRepository()
    private val certificationRepository = CertificationRepository()

    private val _caretakerProfile = MutableStateFlow<CaretakerProfile?>(null)
    val caretakerProfile: StateFlow<CaretakerProfile?> = _caretakerProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadCaretakerProfile(caretakerUid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Fetch user info
                val user = userRepository.getUserById(caretakerUid)

                // Fetch caretaker details
                val details = caretakerDetailsRepository.getCaretakerProfileByUid(caretakerUid)

                if (user != null && details != null) {
                    // Fetch skills and certifications
//                    val skills = skillRepository.getSkillsByCaretakerId(caretakerUid)
                    val certifications = details.certificationIds.mapNotNull { certId ->
                        certificationRepository.getCertificationById(certId)
                    }

                    // Combine into CaretakerProfile
                    _caretakerProfile.value = CaretakerProfile(
                        uid = user.uid,
                        name = user.name,
                        email = user.email,
                        bio = user.bio ?: "", // assuming User has bio field
                        gender = details.gender,
                        age = details.age,
                        yearsOfExperience = details.yearsOfExperience,
                        caretakerType = details.caretakerType,
                        availabilityType = details.availabilityType,
                        nightCare = details.nightCare,
                        skills = emptyList(), //skills,
                        certifications = certifications
                    )
                } else {
                    _error.value = "Caretaker not found"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}