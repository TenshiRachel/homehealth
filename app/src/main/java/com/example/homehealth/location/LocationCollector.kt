package com.example.homehealth.location

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

object LocationCollector {

    @SuppressLint("MissingPermission")
    fun collect(context: Context) {
        val client = LocationServices.getFusedLocationProviderClient(context)

        Log.d("LocationCollector", "Attempting to get current location...")

        // Use getCurrentLocation for more reliability than lastLocation
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("LocationCollector", "--- LOCATION DETECTED ---")
                    Log.d("LocationCollector", "Latitude: ${location.latitude}")
                    Log.d("LocationCollector", "Longitude: ${location.longitude}")
                    Log.d("LocationCollector", "Accuracy: ${location.accuracy}")
                    Log.d("LocationCollector", "Timestamp: ${location.time}")
                    Log.d("LocationCollector", "--------------------------")
                } else {
                    Log.w("LocationCollector", "Location was null even after requesting current location")
                }
            }
            .addOnFailureListener { e ->
                Log.e("LocationCollector", "Failed to get location: ${e.message}")
            }
    }
}
