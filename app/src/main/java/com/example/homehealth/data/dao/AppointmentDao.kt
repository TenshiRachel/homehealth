package com.example.homehealth.data.dao

import android.util.Log
import com.example.homehealth.data.models.Appointment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class AppointmentDao {

    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val APPOINTMENTS_COLLECTION = "Appointments"
    }

    suspend fun createAppointment(appointment: Appointment): Boolean {
        return try {
            Log.d("AppointmentDao", "Creating appointment: ${appointment.id}")

            db.collection(APPOINTMENTS_COLLECTION)
                .document(appointment.id)
                .set(appointment)
                .await()

            true
        } catch (e: Exception) {
            Log.e("AppointmentDao", "Failed to create appointment", e)
            false
        }
    }

    suspend fun getAppointmentById(appointmentId: String): Appointment? {
        return try {
            val doc = db.collection(APPOINTMENTS_COLLECTION)
                .document(appointmentId)
                .get()
                .await()

            doc.toObject(Appointment::class.java)
        } catch (e: Exception) {
            Log.e("AppointmentDao", "Failed to get appointment by id", e)
            null
        }
    }

    suspend fun getAllAppointments(): List<Appointment> {
        return try {
            val snapshot = db.collection(APPOINTMENTS_COLLECTION)
                .get()
                .await()

            snapshot.documents.mapNotNull {
                it.toObject(Appointment::class.java)
            }
        } catch (e: Exception) {
            Log.e("AppointmentDao", "Failed to get all appointments", e)
            emptyList()
        }
    }

    suspend fun getAppointmentsByPatient(patientUid: String): List<Appointment> {
        return try {
            val snapshot = db.collection(APPOINTMENTS_COLLECTION)
                .whereEqualTo("patientUid", patientUid)
                .get()
                .await()

            snapshot.documents.mapNotNull {
                it.toObject(Appointment::class.java)
            }
        } catch (e: Exception) {
            Log.e("AppointmentDao", "Failed to get appointments for patient", e)
            emptyList()
        }
    }

    suspend fun getAppointmentsByCaretaker(caretakerUid: String): List<Appointment> {
        return try {
            val snapshot = db.collection(APPOINTMENTS_COLLECTION)
                .whereEqualTo("caretakerUid", caretakerUid)
                .get()
                .await()

            snapshot.documents.mapNotNull {
                it.toObject(Appointment::class.java)
            }
        } catch (e: Exception) {
            Log.e("AppointmentDao", "Failed to get appointments for caretaker", e)
            emptyList()
        }
    }

    suspend fun updateAppointment(appointment: Appointment): Boolean {
        return try {
            db.collection(APPOINTMENTS_COLLECTION)
                .document(appointment.id)
                .set(appointment, SetOptions.merge())
                .await()

            true
        } catch (e: Exception) {
            Log.e("AppointmentDao", "Failed to update appointment", e)
            false
        }
    }

    suspend fun deleteAppointment(appointmentId: String): Boolean {
        return try {
            db.collection(APPOINTMENTS_COLLECTION)
                .document(appointmentId)
                .delete()
                .await()

            true
        } catch (e: Exception) {
            Log.e("AppointmentDao", "Failed to delete appointment", e)
            false
        }
    }
}