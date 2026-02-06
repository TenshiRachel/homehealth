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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class IndexViewModel : ViewModel() {

    private val appointmentRepository = AppointmentRepository()
    private val chatRepository = ChatRepository()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments

    private val _createdChatId = MutableLiveData<String?>(null)
    val createdChatId = _createdChatId.asFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading


    fun startObservingAppointments(recipientUid: String, isCaretaker: Boolean) {
        viewModelScope.launch {
            appointmentRepository
                .observeAppointmentsForRecipient(recipientUid, isCaretaker)
                .collect { list ->
                    _appointments.value = list
                    _isLoading.value = false
                }
        }
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
