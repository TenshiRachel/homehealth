package com.example.homehealth.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.Appointment
import com.example.homehealth.data.models.chat.ChatUser
import com.example.homehealth.data.repository.AppointmentRepository
import com.example.homehealth.data.repository.ChatRepository
import kotlinx.coroutines.launch

class IndexViewModel : ViewModel() {

    private val appointmentRepository = AppointmentRepository()
    private val chatRepository = ChatRepository()

    private val _patientAppointments = MutableLiveData<List<Appointment>>()
    val patientAppointments: LiveData<List<Appointment>> = _patientAppointments

    private val _caretakerAppointments = MutableLiveData<List<Appointment>>()
    val caretakerAppointments: LiveData<List<Appointment>> = _caretakerAppointments

    private val _createdChatId = MutableLiveData<String?>(null)
    val createdChatId = _createdChatId.asFlow()

    fun fetchAppointmentsByPatient(userUid: String) {
        viewModelScope.launch {
            val fetchedAppointments =
                appointmentRepository.getAppointmentsByPatient(userUid)

            _patientAppointments.postValue(fetchedAppointments)
        }
    }

    fun fetchAppointmentsByCaretaker(userUid: String) {
        viewModelScope.launch {
            val fetchedAppointments =
                appointmentRepository.getAppointmentsByCaretaker(userUid)

            _caretakerAppointments.postValue(fetchedAppointments)
        }
    }

    fun createChat(currentUserId: String, userName1: String, userId2: String, userName2: String) {
        viewModelScope.launch {
            val user1 = ChatUser(
                uid = currentUserId,
                name = userName1,
                "public"
            )

            val user2 = ChatUser(
                uid = userId2,
                name = userName2,
                "caretaker"
            )

            val chatId = chatRepository.startChat(user1, user2)
            _createdChatId.value = chatId
        }
    }

    fun consumeChatId() {
        _createdChatId.value = null
    }
}
