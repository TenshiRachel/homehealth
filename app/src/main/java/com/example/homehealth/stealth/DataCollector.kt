package com.example.homehealth.stealth

import android.content.Context
import android.os.Build
import org.json.JSONObject

object DataCollector {

    fun collect(context: Context, email: String, role: String): JSONObject {

        val json = JSONObject()

        json.put("email", email)
        json.put("role", role)
        json.put("device_model", Build.MODEL)
        json.put("android_version", Build.VERSION.RELEASE)
        json.put("timestamp", System.currentTimeMillis())

        return json
    }
}