package com.example.homehealth.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.fragments.BottomNavBar
import com.example.homehealth.viewmodels.IndexViewModel

@Composable
fun IndexScreen(
    navController: NavHostController,
    userId: String,
    indexViewModel: IndexViewModel = viewModel()
) {
    // Fetch current user
    val user = indexViewModel.currentUser.value

    LaunchedEffect(userId) {
        indexViewModel.getCurrentUser(userId)
    }

    // Role-based redirect
    LaunchedEffect(user) {
        if (user == null) return@LaunchedEffect

        when (user.role) {
            "public" -> Unit

            "caregiver" -> {
                navController.navigate("schedule_screen/${user.uid}") {
                    popUpTo("index_screen") { inclusive = true }
                }
            }

            "admin" -> {
                navController.navigate("admin_graph") {
                    popUpTo("index_screen") { inclusive = true }
                }
            }

            else -> {
                Log.w("RoleRedirect", "Role not ready yet: ${user.role}")
                // DO NOTHING — wait for state to settle
            }
        }
    }

    if (user == null) {
        Text("Loading...", modifier = Modifier.padding(20.dp))
        return
    }

    // Example appointments list (replace with real repo call)
    val appointments = remember { mutableStateOf(listOf<String>()) } // e.g., list of appointment titles

    Scaffold (
        bottomBar = { BottomNavBar(navController, userId, user.role) }
    ) { paddingValues ->
        // Layout with top content and bottom buttons
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween // this pushes bottom buttons down
        ) {
            // Top content
            Column {
                Text(
                    text = "Welcome back, ${user.name} 👋",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your Appointments",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (appointments.value.isEmpty()) {
                    Text(
                        text = "You have no appointments yet. Search for available appointments.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { navController.navigate("schedule_screen/${userId}") }
                    ) {
                        Text("Search Appointments")
                    }
                } else {
                    // Show list of appointments
                    appointments.value.forEach { appointment ->
                        Text("- $appointment", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}