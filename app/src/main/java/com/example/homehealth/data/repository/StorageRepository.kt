package com.example.homehealth.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageRepository {
    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadFile(uri: Uri, path: String): Result<String> {
        return try {
            val ref = storage.reference.child(path)

            // Upload the file
            ref.putFile(uri).await()

            // Get download URL
            val downloadUrl = ref.downloadUrl.await().toString()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e("StorageRepository", "Upload failed: ${e.message}")
            Result.failure(e)
        }
    }

    // Track upload progress
    fun uploadFileWithProgress(
        uri: Uri,
        path: String,
        onProgress: (Double) -> Unit,
        onComplete: (String?) -> Unit
    ) {
        val ref = storage.reference.child(path)
        val uploadTask = ref.putFile(uri)

        uploadTask
            .addOnProgressListener { task ->
                val progress = (100.0 * task.bytesTransferred / task.totalByteCount)
                onProgress(progress)
            }
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { url ->
                    onComplete(url.toString())
                }
            }
            .addOnFailureListener {
                Log.e("StorageRepository", "Error: ${it.message}")
                onComplete(null)
            }
    }
}