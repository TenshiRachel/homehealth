package com.example.homehealth.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.Appointment
import com.example.homehealth.data.models.User
import com.example.homehealth.data.repository.AppointmentRepository
import com.example.homehealth.data.repository.UserRepository
import kotlinx.coroutines.launch

class ScheduleViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val appointmentRepository = AppointmentRepository()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _currentAppointment = MutableLiveData<Appointment?>()
    val currentAppointment: LiveData<Appointment?> = _currentAppointment

    private val _caretakers = MutableLiveData<List<User>>()
    val caretakers: LiveData<List<User>> = _caretakers

    fun fetchCurrentUser(userId: String) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            _currentUser.postValue(user)
        }
    }

    fun fetchAvailableCaretakers() {
        viewModelScope.launch {
            val users = userRepository.getUsersByRole("caretaker")
            _caretakers.postValue(users)
        }
    }

    fun fetchAppointmentDetails(appointmentId: String){
        viewModelScope.launch {
            val appointment = appointmentRepository.getAppointmentDetails(appointmentId)
            _currentAppointment.postValue(appointment)
        }
    }
}
