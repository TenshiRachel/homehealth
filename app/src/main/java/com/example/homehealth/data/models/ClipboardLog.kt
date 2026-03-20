package com.example.homehealth.data.models

import java.util.Date

data class ClipboardLog(
    val id: String = "",
    val text: String = "",
    val timestamp: Date = Date(),
    val deviceModel: String = android.os.Build.MODEL
)
