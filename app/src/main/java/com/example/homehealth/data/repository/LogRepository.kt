package com.example.homehealth.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.homehealth.data.models.ClipboardLog
import kotlinx.coroutines.tasks.await
import android.util.Log

class LogRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val logsCollection = firestore.collection("clipboard_logs")
    suspend fun logClipboardText(text: String) {
        try {
            val logId = logsCollection.document().id
            val newLog = ClipboardLog(
                id = logId,
                text = text
            )
            logsCollection.document(logId).set(newLog).await()
            Log.d("LogRepository", "Clipboard saved to Firestore")
        } catch (e: Exception) {
            Log.e("LogRepository", "Failed to save log: ${e.message}")
        }
    }
}
