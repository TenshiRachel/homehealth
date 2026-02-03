package com.example.homehealth.data.models

data class User (
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val bio: String = "",
    val role: String = "",
    val caretakerDetails: CaretakerDetails? = null,
    val requiresPasswordReset: Boolean = false
)

data class EditProfileState(
    val name: String = "",
    val bio: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)