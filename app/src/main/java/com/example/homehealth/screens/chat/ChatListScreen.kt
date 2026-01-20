package com.example.homehealth.screens.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.fragments.BottomNavBar
import com.example.homehealth.viewmodels.ChatListViewModel

@Composable
fun ChatListScreen(navController: NavHostController,
                   userId: String,
                   chatListViewModel: ChatListViewModel = viewModel()
){
    val userChats by chatListViewModel.chats.observeAsState()

    LaunchedEffect(userId) {
        chatListViewModel.fetchUserChats(userId)
    }

    Scaffold (
        bottomBar = { BottomNavBar(navController, userId, "public") }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            items(userChats!!){ chat ->

            }
        }
    }
}