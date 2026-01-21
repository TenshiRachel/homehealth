package com.example.homehealth.screens.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.data.models.chat.Chat
import com.example.homehealth.fragments.BottomNavBar
import com.example.homehealth.utils.formatTimestamp
import com.example.homehealth.viewmodels.ChatListViewModel

@Composable
fun ChatListScreen(navController: NavHostController,
                   userId: String,
                   chatListViewModel: ChatListViewModel = viewModel()
){
    val currentUser by chatListViewModel.currentUser.observeAsState(null)
    val userChats by chatListViewModel.chats.observeAsState(emptyList())

    LaunchedEffect(userId) {
        chatListViewModel.fetchCurrentUser(userId)
        chatListViewModel.fetchUserChats(userId)
    }

    if (currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold (
        bottomBar = { BottomNavBar(navController, userId, currentUser!!.role) }
    ) { paddingValues ->
        if (userChats.isEmpty()){
            EmptyChat(Modifier.padding(paddingValues))
        }
        else{
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                items(userChats){ chat ->
                    ChatListItem(
                        chat = chat,
                        currentUserId = userId,
                        onClick = {  }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatListItem(
    chat: Chat,
    currentUserId: String,
    onClick: () -> Unit
) {
    val otherUser = chat.members.firstOrNull {
        it.uid != currentUserId
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "User",
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = otherUser?.name ?: "Unknown",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = chat.lastMessage.ifBlank { "No messages yet" },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = formatTimestamp(chat.lastMessageTime),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }

    HorizontalDivider()
}

@Composable
fun EmptyChat(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = "No chats",
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("No chats yet", color = Color.Gray)
        }
    }
}