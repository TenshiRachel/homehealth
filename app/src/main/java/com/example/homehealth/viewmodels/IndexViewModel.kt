package com.example.homehealth.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.Appointment
import com.example.homehealth.data.models.User
import com.example.homehealth.data.models.chat.ChatUser
import com.example.homehealth.data.repository.AppointmentRepository
import com.example.homehealth.data.repository.ChatRepository
import com.example.homehealth.data.repository.UserRepository
import kotlinx.coroutines.launch

class IndexViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val appointmentRepository = AppointmentRepository()
    private val chatRepository = ChatRepository()

    var currentUser = mutableStateOf<User?>(null)
        private set

    var appointments = mutableStateOf<List<Appointment>>(emptyList())
        private set

    private val _createdChatId = MutableLiveData<String?>(null)
    val createdChatId = _createdChatId.asFlow()

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
