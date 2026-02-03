package com.example.homehealth.data.models

import com.example.homehealth.data.enums.AvailabilityType
import com.example.homehealth.data.enums.CaretakerType
import com.example.homehealth.data.enums.Gender

data class CaretakerDetails(
    val uid: String = "",
    val gender: Gender = Gender.UNSPECIFIED,
    val age: Int = 0,
    val yearsOfExperience: Int = 0,
    val caretakerType: CaretakerType = CaretakerType.UNKNOWN,
    val availabilityType: AvailabilityType = AvailabilityType.UNKNOWN,
    val nightCare: Boolean = false,
    val certificationIds: List<String> = emptyList(),
    val certificationProofs: Map<String, String> = emptyMap(),
    val skillIds: List<String> = emptyList()
)
