package com.example.homehealth.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.Appointment
import com.example.homehealth.data.models.User
import com.example.homehealth.data.repository.AppointmentRepository
import com.example.homehealth.data.repository.UserRepository
import kotlinx.coroutines.launch

class IndexViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val appointmentRepository = AppointmentRepository()

    var currentUser = mutableStateOf<User?>(null)
        private set

    var appointments = mutableStateOf<List<Appointment>>(emptyList())
        private set

    fun getCurrentUser(userId: String) {
        viewModelScope.launch {
            currentUser.value = userRepository.getUserById(userId)
        }
    }

    fun fetchAppointments(patientUid: String) {
        viewModelScope.launch {
            appointments.value =
                appointmentRepository.getAppointmentsByPatient(patientUid)
        }
    }
}
