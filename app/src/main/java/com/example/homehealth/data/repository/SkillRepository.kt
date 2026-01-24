package com.example.homehealth.data.repository

import com.example.homehealth.data.dao.SkillDao
import com.example.homehealth.data.models.Skill

class SkillRepository(
    private val skillDao: SkillDao = SkillDao()
) {

    suspend fun getAllSkills(): List<Skill> {
        return skillDao.getAllSkills()
    }

    suspend fun getSkillById(SkillId: String): Skill? {
        return skillDao.getSkillById(SkillId)
    }

    suspend fun createSkill(Skill: Skill) : Boolean {
        return skillDao.createSkill(Skill)
    }

    suspend fun updateSkill(Skill: Skill): Boolean {
        return skillDao.updateSkill(Skill)
    }

    suspend fun deleteSkill(SkillId: String): Boolean {
        return skillDao.deleteSkill(SkillId)
    }
}