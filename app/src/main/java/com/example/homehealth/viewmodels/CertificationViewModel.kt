package com.example.homehealth.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.Certification
import com.example.homehealth.data.repository.CertificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CertificationViewModel : ViewModel() {
    private val certificationRepository = CertificationRepository()

    private val _certifications = MutableStateFlow<List<Certification>>(emptyList())
    val certifications: StateFlow<List<Certification>> = _certifications

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _createSuccess = mutableStateOf(false)
    val createSuccess: State<Boolean> = _createSuccess

    fun loadCertifications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _certifications.value = certificationRepository.getAllCertifications()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createCertification(name: String) {
        viewModelScope.launch {
            val normalizedName = name.trim().lowercase()

            if (normalizedName.isBlank()) {
                _errorMessage.value = "Certification name cannot be empty"
                return@launch
            }

            if (certificationRepository.certificationExists(normalizedName)) {
                _errorMessage.value = "Certification already exists"
                return@launch
            }

            val success = certificationRepository.createCertification(
                Certification(name = normalizedName)
            )

            if (success) {
                _createSuccess.value = true
            } else {
                _errorMessage.value = "Failed to create certification"
            }
        }
    }

    fun updateCertification(certification: Certification) {
        viewModelScope.launch {
            val success = certificationRepository.updateCertification(certification)
            if (success) loadCertifications()
            else _errorMessage.value = "Failed to update certification"
        }
    }

    fun deleteCertification(certificationId: String) {
        viewModelScope.launch {
            val success = certificationRepository.deleteCertification(certificationId)
            if (success) loadCertifications()
            else _errorMessage.value = "Failed to delete certification"
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun resetCreateSuccess() {
        _createSuccess.value = false
    }
}