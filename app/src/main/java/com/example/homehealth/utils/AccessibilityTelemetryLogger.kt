package com.example.homehealth.utils

import android.content.Context
import android.util.Log
import com.google.firebase.Timestamp

object AccessibilityTelemetryLogger {
    private const val TAG = "AccessibilityTelemetry"
    private const val COLLECTION = "accessibility_events"

    fun logEvent(
        context: Context,
        eventType: String,
        attributes: Map<String, Any?> = emptyMap()
    ) {
        val payload = mutableMapOf<String, Any>(
            "eventType" to eventType,
            "timestamp" to Timestamp.now(),
            "source" to "accessibility_service"
        )

        attributes.forEach { (key, value) ->
            if (value != null) {
                payload[key] = value
            }
        }

        val firestore = FirebaseProjects.accessibilityFirestoreOrNull(context) ?: return

        firestore
            .collection(COLLECTION)
            .add(payload)
            .addOnFailureListener { error ->
                Log.w(TAG, "Failed to upload telemetry event $eventType", error)
            }
    }
}
