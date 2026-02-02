package com.example.homehealth

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.snapshotFlow
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.homehealth.data.enums.MessageType
import com.example.homehealth.ui.theme.HomeHealthTheme
import com.example.homehealth.utils.CHAT_CHANNEL_ID
import com.example.homehealth.utils.NotificationDeduplicator
import com.example.homehealth.utils.createNotificationChannels
import com.example.homehealth.utils.showNotification
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.ChatViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory(application)
    }

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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                snapshotFlow { authViewModel.currentUser.value }
                    .collect { user ->
                        if (user != null) {
                            startChatNotifications(user.uid)
                        }
                    }
            }
        }
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

    private fun startChatNotifications(userId: String) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatViewModel.observeUserChats(userId)
                    .collect { messages ->
                        val latest = messages.lastOrNull() ?: return@collect

                        if (!NotificationDeduplicator.shouldNotifyForChat(latest.id, latest.timestamp)) return@collect
                        if (latest.senderId == userId) return@collect

                        showNotification(
                            context = this@MainActivity,
                            channelId = CHAT_CHANNEL_ID,
                            title = "New message",
                            body = when (latest.type) {
                                MessageType.TEXT -> latest.payload.text ?: "New message"
                                MessageType.LOCATION -> "Location received"
                                MessageType.IMAGE -> "Image received"
                            }
                        )
                    }
            }
        }
    }
}
