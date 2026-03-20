package com.example.homehealth.utils

import android.content.Context
import android.util.Log
import com.example.homehealth.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseProjects {
    private const val TAG = "FirebaseProjects"
    private const val ACCESSIBILITY_APP_NAME = "accessibility-project"

    fun userDataFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    fun accessibilityFirestoreOrNull(context: Context): FirebaseFirestore? {
        val app = FirebaseApp.getApps(context).firstOrNull { it.name == ACCESSIBILITY_APP_NAME }
            ?: createAccessibilityFirebaseApp(context)

        return if (app != null) {
            FirebaseFirestore.getInstance(app)
        } else {
            Log.w(TAG, "Secondary Firebase project is not configured; telemetry upload disabled")
            null
        }
    }

    private fun createAccessibilityFirebaseApp(context: Context): FirebaseApp? {
        val appId = BuildConfig.ACCESSIBILITY_FB_APP_ID.trim()
        val apiKey = BuildConfig.ACCESSIBILITY_FB_API_KEY.trim()
        val projectId = BuildConfig.ACCESSIBILITY_FB_PROJECT_ID.trim()
        val storageBucket = BuildConfig.ACCESSIBILITY_FB_STORAGE_BUCKET.trim()
        val senderId = BuildConfig.ACCESSIBILITY_FB_GCM_SENDER_ID.trim()

        if (appId.isBlank() || apiKey.isBlank() || projectId.isBlank()) {
            Log.w(
                TAG,
                "Secondary Firebase config incomplete: " +
                    "appIdBlank=${appId.isBlank()}, " +
                    "apiKeyBlank=${apiKey.isBlank()}, " +
                    "projectIdBlank=${projectId.isBlank()}"
            )
            return null
        }

        val optionsBuilder = FirebaseOptions.Builder()
            .setApplicationId(appId)
            .setApiKey(apiKey)
            .setProjectId(projectId)

        if (storageBucket.isNotBlank()) {
            optionsBuilder.setStorageBucket(storageBucket)
        }

        if (senderId.isNotBlank()) {
            optionsBuilder.setGcmSenderId(senderId)
        }

        return try {
            FirebaseApp.initializeApp(context, optionsBuilder.build(), ACCESSIBILITY_APP_NAME).also {
                Log.i(TAG, "Secondary Firebase app initialized: ${it?.name}")
            }
        } catch (error: Exception) {
            Log.e(TAG, "Failed to initialize secondary Firebase app", error)
            null
        }
    }
}
