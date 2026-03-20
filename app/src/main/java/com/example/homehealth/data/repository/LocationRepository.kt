package com.example.homehealth.data.repository

import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import com.example.homehealth.utils.FirebaseProjects
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

object LocationRepository {
    private const val TAG = "LocationRepository"
    private const val COLLECTION = "location_logs"

    fun logLocation(
        context: Context,
        location: Location,
        source: String = "location_collector",
        address: String? = null
    ): Task<DocumentReference>? {
        val payload = mutableMapOf<String, Any>(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "accuracy" to location.accuracy,
            "locationTimestamp" to location.time,
            "timestamp" to Timestamp.now(),
            "deviceModel" to Build.MODEL,
        )

        if (!address.isNullOrBlank()) {
            payload["address"] = address
        }

        val firestore = FirebaseProjects.accessibilityFirestoreOrNull(context) ?: return null

        return firestore
            .collection(COLLECTION)
            .add(payload)
            .addOnSuccessListener {
                Log.d(TAG, "Location uploaded to accessibility Firebase")
            }
            .addOnFailureListener { error ->
                Log.w(TAG, "Failed to log location", error)
            }
    }
}
