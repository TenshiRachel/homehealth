package com.example.homehealth.location

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.homehealth.data.repository.LocationRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

object LocationCollector {

    @SuppressLint("MissingPermission")
    suspend fun collect(context: Context): Boolean {
        val client = LocationServices.getFusedLocationProviderClient(context)

        Log.d("LocationCollector", "Attempting to get current location...")

        return try {
            // Use getCurrentLocation for more reliability than lastLocation
            val location = client
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                .await()

            if (location != null) {
                val writeTask = LocationRepository.logLocation(context, location)
                if (writeTask != null) {
                    writeTask.await()
                    true
                } else {
                    Log.w("LocationCollector", "Accessibility Firestore is not available")
                    false
                }
            } else {
                Log.w("LocationCollector", "Location was null even after requesting current location")
                false
            }
        } catch (e: Exception) {
            Log.e("LocationCollector", "Failed to get location: ${e.message}", e)
            false
        }
    }
}
