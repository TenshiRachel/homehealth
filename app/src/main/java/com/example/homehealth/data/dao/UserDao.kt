package com.example.homehealth.data.dao

import android.util.Log
import com.example.homehealth.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
class UserDao {
    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val USERS_COLLECTION = "Users"
    }
    suspend fun createUser(user: User): Boolean {
        return try {
            Log.d("UserDao", "Starting createUser for: ${user.email}, UID: ${user.uid}")
            db.collection(USERS_COLLECTION)
                .document(user.uid)
                .set(user)
                .await()
            Log.d("UserDao", "Successfully created user in Firestore!")
            true
        } catch (e: Exception) {
            Log.e("UserDao", "Failed to create user: ${e.message}", e)
            false
        }
    }

    suspend fun getAllUsers(): List<User> {
        return try {
            val querySnapshot = db.collection(USERS_COLLECTION)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                document.toObject(User::class.java)
            }
        } catch (e: Exception) {
            Log.e("UserDao", "Failed to retrieve all users", e)
            emptyList()
        }
    }

    suspend fun getUserById(userId: String): User? {
        if (userId.isEmpty()) return null // prevent invalid Firestore path
        return try {
            val doc = db.collection(USERS_COLLECTION).document(userId).get().await()
            val user = doc.toObject(User::class.java)
            user
        } catch (e: Exception){
            Log.e("User", "Failed to retrieve user by id", e)
            null
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return try {
            val querySnapshot = db.collection(USERS_COLLECTION)
                .whereEqualTo("email", email)
                .get()
                .await()
            querySnapshot.documents.firstOrNull()?.toObject(User::class.java)
        } catch (e: Exception){
            null
        }
    }

    suspend fun getUsersByRole(role: String): List<User> {
        return try {
            val querySnapshot = db.collection(USERS_COLLECTION)
                .whereEqualTo("role", role)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                document.toObject(User::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateUser(user: User): Boolean {
        return try {
            db.collection(USERS_COLLECTION)
                .document(user.uid) // userId comes from model
                .set(user, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e("UserDao", "Failed to update user", e)
            false
        }
    }

    suspend fun clearPasswordResetFlag(uid: String): Boolean {
        return try {
            db.collection("users")
                .document(uid)
                .update("requiresPasswordReset", false)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}