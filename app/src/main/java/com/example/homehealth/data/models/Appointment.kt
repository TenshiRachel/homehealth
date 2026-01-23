package com.example.homehealth.data.models

data class Appointment (
    val id: String = "",
    val patientUid: String = "",
    val caretakerUid: String = "",
    val caretakerName: String = "",
    val name: String = "",
    val description: String = "",
    val bookingDateTime: String = "",
    val apptDateTime: String = "",
    val location: String = "",
    val status: String = ""
)