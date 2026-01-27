package com.example.homehealth.data.dao

import android.util.Log
import com.example.homehealth.data.models.CaretakerDetails
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CaretakerDetailsDao {
    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val CARETAKER_COLLECTION = "caretakerDetails"
    }

    suspend fun createCaretakerDetails(
        details: CaretakerDetails
    ): Boolean {
        return try {
            db.collection(CARETAKER_COLLECTION)
                .document(details.uid)
                .set(details)
                .await()
            true
        } catch (e: Exception) {
            Log.e("CaretakerDetailsDao", "Failed to create caretaker details", e)
            false
        }
    }

    suspend fun getCaretakerDetailsById(
        uid: String
    ): CaretakerDetails? {
        if (uid.isBlank()) return null

        return try {
            db.collection(CARETAKER_COLLECTION)
                .document(uid)
                .get()
                .await()
                .toObject(CaretakerDetails::class.java)
        } catch (e: Exception) {
            Log.e("CaretakerDetailsDao", "Failed to retrieve caretaker details", e)
            null
        }
    }

    suspend fun updateCaretakerDetails(
        details: CaretakerDetails
    ): Boolean {
        return try {
            db.collection(CARETAKER_COLLECTION)
                .document(details.uid)
                .set(details)
                .await()
            true
        } catch (e: Exception) {
            Log.e("CaretakerDetailsDao", "Failed to update caretaker details", e)
            false
        }
    }

    suspend fun deleteCaretakerDetails(uid: String): Boolean {
        return try {
            db.collection(CARETAKER_COLLECTION)
                .document(uid)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("CaretakerDetailsDao", "Failed to delete caretaker details", e)
            false
        }
    }
}
