package com.example.homehealth.data.models

import com.example.homehealth.data.enums.AvailabilityType
import com.example.homehealth.data.enums.CaretakerType

data class CaretakerDetails(
    val uid: String = "",
    val gender: String = "",
    val age: Int = 0,
    val yearsOfExperience: Int = 0,
    val caretakerType: CaretakerType = CaretakerType.FULL_TIME,
    val availabilityType: AvailabilityType = AvailabilityType.WEEKDAYS,
    val nightCare: Boolean = false,
    val certificationIds: List<String> = emptyList()
)
