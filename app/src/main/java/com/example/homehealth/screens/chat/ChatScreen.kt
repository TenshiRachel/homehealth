package com.example.homehealth.screens.chat

import android.Manifest
import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.data.models.chat.Message
import com.example.homehealth.data.models.chat.MessageType
import com.example.homehealth.utils.formatTimestamp
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.ChatViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChatScreen(navController: NavHostController,
               chatId: String,
               chatViewmodel: ChatViewModel = viewModel(
                   factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application)
               ),
               authViewModel: AuthViewModel = viewModel()
){
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    val chat by chatViewmodel.chat.observeAsState(null)
    val messages by chatViewmodel.messages.observeAsState(emptyList())

    val sessionUser = authViewModel.currentUser.value

    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(chatId) {
        chatViewmodel.fetchChat(chatId)
        chatViewmodel.fetchMessages(chatId)
    }

    if (sessionUser == null || chat == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val otherUser = chat!!.members.first { it.uid != sessionUser.uid }

    Scaffold(
        topBar = {
            ChatTopBar(
                chatPartnerName = otherUser.name,
                onBack = { navController.popBackStack() }
            )
        },
        bottomBar = {
            ChatInputBar(
                text = messageText,
                onTextChange = { messageText = it },
                onSend = {
                    if (messageText.isNotBlank()) {
                        chatViewmodel.sendMessage(
                            chatId = chatId,
                            senderId = sessionUser.uid,
                            recipientId = otherUser.uid,
                            text = messageText
                        )
                        messageText = ""
                    }
                },
                onShareLocation = {
                    if (permissionState.status.isGranted) {
                        chatViewmodel.sendLocation(
                            chatId = chatId,
                            senderId = sessionUser.uid,
                            recipientId = otherUser.uid
                        )
                    }
                    else {
                        permissionState.launchPermissionRequest()
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                ChatBubble(
                    message = message,
                    isOwnMessage = message.senderId == sessionUser.uid
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    chatPartnerName: String,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = chatPartnerName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}

@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onShareLocation: () -> Unit
) {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onShareLocation
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Share Location"
                )
            }
            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message") },
                maxLines = 4
            )

            IconButton(
                onClick = onSend,
                enabled = text.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send"
                )
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: Message,
    isOwnMessage: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = if (isOwnMessage)
            Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isOwnMessage)
                Alignment.End else Alignment.Start
        ) {
            Surface(
                color = if (isOwnMessage)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            ) {
                when (message.type) {
                    MessageType.TEXT -> {
                        Text(
                            text = message.payload.text!!,
                            modifier = Modifier.padding(12.dp),
                            color = if (isOwnMessage)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    MessageType.LOCATION -> {
                        val lat = message.payload.latitude ?: 0.0
                        val long = message.payload.longitude ?: 0.0

                        LocationPreview(
                            latitude = lat,
                            longitude = long
                        )
                    }

                    MessageType.IMAGE -> {

                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = formatTimestamp(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LocationPreview(
    latitude: Double,
    longitude: Double
) {
    val location = LatLng(latitude, longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 15f)
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp)),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            scrollGesturesEnabled = false
        )
    ) {
        Marker(
            state = MarkerState(position = location)
        )
    }
}