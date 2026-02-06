package com.example.homehealth.data.repository

import com.example.homehealth.data.dao.AppointmentDao
import com.example.homehealth.data.models.Appointment
import kotlinx.coroutines.flow.Flow

class AppointmentRepository(private val appointmentDao: AppointmentDao = AppointmentDao()) {
    suspend fun createAppointment(appointment: Appointment) : Boolean {
        return appointmentDao.createAppointment(appointment)
    }

//    suspend fun getAllAppointments(): List<Appointment> {
//        return appointmentDao.getAllAppointments()
//    }

    fun observeAppointmentsForRecipient(recipientUid: String, isCaretaker: Boolean): Flow<List<Appointment>> {
        return appointmentDao.observeAppointmentsForRecipient(recipientUid, isCaretaker)
    }

    fun observeAppointmentById(appointmentId: String): Flow<Appointment?> {
        return appointmentDao.observeAppointmentById(appointmentId)
    }


    suspend fun getAppointmentDetails(appointmentId: String): Appointment? {
        return appointmentDao.getAppointmentById(appointmentId)
    }

    suspend fun getAppointmentsByPatient(patientUid: String): List<Appointment> {
        return appointmentDao.getAppointmentsByPatient(patientUid)
    }

    suspend fun getAppointmentsByCaretaker(caretakerUid: String): List<Appointment> {
        return appointmentDao.getAppointmentsByCaretaker(caretakerUid)
    }

    suspend fun updateAppointment(appointment: Appointment): Boolean {
        return appointmentDao.updateAppointment(appointment)
    }

    suspend fun deleteAppointment(appointmentId: String): Boolean {
        return appointmentDao.deleteAppointment(appointmentId)
    }
}