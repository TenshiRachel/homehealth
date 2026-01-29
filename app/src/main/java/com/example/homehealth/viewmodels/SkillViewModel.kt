package com.example.homehealth.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homehealth.data.models.Skill
import com.example.homehealth.data.repository.SkillRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SkillViewModel : ViewModel() {
    private val skillRepository = SkillRepository()

    private val _skills = MutableStateFlow<List<Skill>>(emptyList())
    val skills: StateFlow<List<Skill>> = _skills

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun loadSkills() {
        viewModelScope.launch {
            _isLoading.value = true
            _skills.value = skillRepository.getAllSkills()
            _isLoading.value = false
        }
    }

    fun createSkill(name: String) {
        viewModelScope.launch {
            val success = skillRepository.createSkill(Skill(name = name))
            if (success) loadSkills()
        }
    }

    fun updateSkill(skill: Skill) {
        viewModelScope.launch {
            val success = skillRepository.updateSkill(skill)
            if (success) loadSkills()
        }
    }

    fun deleteSkill(skillId: String) {
        viewModelScope.launch {
            skillRepository.deleteSkill(skillId)
            loadSkills()
        }
    }
}