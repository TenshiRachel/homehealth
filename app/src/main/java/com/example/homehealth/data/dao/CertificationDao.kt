package com.example.homehealth.data.dao

import com.example.homehealth.data.models.Certification
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore

class CertificationDao {
    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val CERTIFICATION_COLLECTION = "Certification"
    }

    suspend fun getAllCertifications(): List<Certification> {
        return try {
            val snapshot = db.collection(CERTIFICATION_COLLECTION)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Certification::class.java)
                    ?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }

    }

    suspend fun getCertificationById(certificationId: String): Certification? {
        return try {
            val doc = db.collection(CERTIFICATION_COLLECTION)
                .document(certificationId)
                .get()
                .await()

            doc.toObject(Certification::class.java)
                ?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun certificationExists(name: String): Boolean {
        val snapshot = db.collection(CERTIFICATION_COLLECTION)
            .whereEqualTo("name", name)
            .get()
            .await()

        return !snapshot.isEmpty
    }

    suspend fun createCertification(certification: Certification): Boolean {
        return try {
            db.collection(CERTIFICATION_COLLECTION)
                .add(
                    mapOf(
                        "name" to certification.name
                    )
                )
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateCertification(certification: Certification): Boolean {
        return try {
            db.collection(CERTIFICATION_COLLECTION)
                .document(certification.id)
                .update("name", certification.name)
                .await()
            true
        } catch (e: Exception) {
            false
        }

    }

    suspend fun deleteCertification(certificationId: String): Boolean {
        return try {
            db.collection(CERTIFICATION_COLLECTION)
                .document(certificationId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}