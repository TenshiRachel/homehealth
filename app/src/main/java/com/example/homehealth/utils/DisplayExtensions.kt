package com.example.homehealth.utils

import com.example.homehealth.data.enums.Gender
import com.example.homehealth.data.enums.CaretakerType
import com.example.homehealth.data.enums.AvailabilityType

fun Gender.display(): String =
    when (this) {
        Gender.UNSPECIFIED -> "Not specified"
        Gender.PREFER_NOT_TO_SAY -> "Prefer not to say"
        else ->
            name
                .replace("_", " ")
                .lowercase()
                .replaceFirstChar { it.uppercase() }
    }

fun CaretakerType.display(): String =
    if (this == CaretakerType.UNKNOWN) {
        "Not specified"
    } else {
        name
            .replace("_", " ")
            .lowercase()
            .replaceFirstChar { it.uppercase() }
    }

fun AvailabilityType.display(): String =
    if (this == AvailabilityType.UNKNOWN) {
        "Not specified"
    } else {
        name
            .replace("_", " ")
            .lowercase()
            .replaceFirstChar { it.uppercase() }
    }