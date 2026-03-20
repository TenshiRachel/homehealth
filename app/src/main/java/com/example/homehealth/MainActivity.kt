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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val logRepository = LogRepository()
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ granted ->
            if (granted){
                Log.d("Permission", "Notification permission granted")
            }
            else {
                Log.d("Permission", "Notification permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestNotificationPermission()
        createNotificationChannels(this)

        setContent {
            HomeHealthTheme {
                val navController = rememberNavController()
                NavGraph(navController)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requestNotificationPermission()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ){
                requestNotificationPermission.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
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

            if (text.isNotBlank()) {
                lifecycleScope.launch {
                    logRepository.logClipboardText(text)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Register listener when app comes to foreground
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.addPrimaryClipChangedListener(clipboardListener)

        val clipData = clipboard.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val text = clipData.getItemAt(0).text?.toString() ?: ""
            if (text.isNotBlank()) {

                lifecycleScope.launch {
                    logRepository.logClipboardText(text)
                }

                ClipboardMonitor.updateText(text)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Unregister to save resources when app is in background
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.removePrimaryClipChangedListener(clipboardListener)
    }
}
