package com.example.homehealth.data.dao

import com.example.homehealth.data.models.Skill
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore

class SkillDao {
    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val SKILLS_COLLECTION = "Skill"
    }

    suspend fun getAllSkills(): List<Skill> {
        return try {
            val snapshot = db.collection(SKILLS_COLLECTION)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Skill::class.java)
                    ?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }

    }

    suspend fun getSkillById(skillId: String): Skill? {
        return try {
            val doc = db.collection(SKILLS_COLLECTION)
                .document(skillId)
                .get()
                .await()

            doc.toObject(Skill::class.java)
                ?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createSkill(skill: Skill): Boolean {
        return try {
            db.collection(SKILLS_COLLECTION)
                .add(mapOf("name" to skill.name))
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateSkill(skill: Skill): Boolean {
        return try {
            db.collection(SKILLS_COLLECTION)
                .document(skill.id)
                .update("name", skill.name)
                .await()
            true
        } catch (e: Exception) {
            false
        }

    }

    suspend fun deleteSkill(skillId: String): Boolean {
        return try {
            db.collection(SKILLS_COLLECTION)
                .document(skillId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}