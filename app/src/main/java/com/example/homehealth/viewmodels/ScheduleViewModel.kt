package com.example.homehealth.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.Appointment
import com.example.homehealth.data.models.User
import com.example.homehealth.data.repository.AppointmentRepository
import com.example.homehealth.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository()
    private val appointmentRepository = AppointmentRepository()

    private val _currentAppointment = MutableLiveData<Appointment?>()
    val currentAppointment: LiveData<Appointment?> = _currentAppointment

    private val _caretakers = MutableLiveData<List<User>>()
    val caretakers: LiveData<List<User>> = _caretakers

    private val _selectedCaretakerName = MutableLiveData<String>()
    val selectedCaretakerName: LiveData<String> = _selectedCaretakerName

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

    fun requestAppointment(
        appointment: Appointment,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                appointmentRepository.createAppointment(appointment)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to create appointment")
            }
        }
    }

    fun fetchSelectedCaretakerName(caretakerId: String){
        viewModelScope.launch {
            val user = userRepository.getUserById(caretakerId)
            _selectedCaretakerName.postValue(user?.name)
        }
    }

    fun observeAppointmentsForRecipient(recipientUid: String, isCaretaker: Boolean): StateFlow<List<Appointment>> {
        return appointmentRepository.observeAppointmentsForRecipient(recipientUid, isCaretaker)
            .map { list ->
                list.sortedByDescending { parseDate(it.bookingDateTime) } // Latest first
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    // Helper function to parse string date
    private fun parseDate(dateStr: String): Long {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
            sdf.parse(dateStr)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }


    fun markAppointmentBooked(appointmentId: String) {
        viewModelScope.launch {
            try {
                val appointment = appointmentRepository.getAppointmentDetails(appointmentId)
                    ?: return@launch

                val updatedAppointment = appointment.copy(
                    status = "BOOKED"
                )

                appointmentRepository.updateAppointment(updatedAppointment)

                // Update UI state
                _currentAppointment.postValue(updatedAppointment)

            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Failed to mark appointment booked", e)
            }
        }
    }

    fun markAppointmentCompleted(appointmentId: String) {
        viewModelScope.launch {
            try {
                val appointment = appointmentRepository.getAppointmentDetails(appointmentId)
                    ?: return@launch

                val updatedAppointment = appointment.copy(
                    status = "COMPLETED"
                )

                appointmentRepository.updateAppointment(updatedAppointment)

                // Update UI state
                _currentAppointment.postValue(updatedAppointment)

            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Failed to mark appointment completed", e)
            }
        }
    }

}
