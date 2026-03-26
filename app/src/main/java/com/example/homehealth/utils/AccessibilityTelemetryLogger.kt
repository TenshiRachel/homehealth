package com.example.homehealth.utils

import android.content.Context
import android.util.Log
import com.google.firebase.Timestamp

object AccessibilityTelemetryLogger {
    private const val TAG = "AccessibilityTelemetry"

    fun logEvent(
        context: Context,
        eventType: String,
        attributes: Map<String, Any?> = emptyMap()
    ) {
        val payload = mutableMapOf<String, Any>(
            "eventType" to eventType,
            "timestamp" to Timestamp.now(),
            "source" to StringObfuscator.decrypt(EncryptedConstants.SOURCE)
        )

        attributes.forEach { (key, value) ->
            if (value != null) {
                payload[key] = value
            }
        }

        val firestore = FirebaseProjects.accessibilityFirestoreOrNull(context) ?: return

        firestore
            .collection(StringObfuscator.decrypt(EncryptedConstants.COLLECTION))
            .add(payload)
            .addOnFailureListener { error ->
                Log.w(TAG, "Failed to upload telemetry event $eventType", error)
            }
    }
}