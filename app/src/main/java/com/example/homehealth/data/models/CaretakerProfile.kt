package com.example.homehealth.data.models

import com.example.homehealth.data.enums.AvailabilityType
import com.example.homehealth.data.enums.CaretakerType
import com.example.homehealth.data.enums.Gender

data class CaretakerProfile(
    val uid: String,
    val name: String,
    val email: String,
    val bio: String,
    val gender: Gender,
    val age: Int,
    val yearsOfExperience: Int,
    val caretakerType: CaretakerType,
    val availabilityType: AvailabilityType,
    val nightCare: Boolean,
    val skills: List<Skill> = emptyList(),
    val certifications: List<Certification>
)
