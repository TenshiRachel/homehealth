package com.example.homehealth.screens

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
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.IndexViewModel

@Composable
fun IndexScreen(
    navController: NavHostController,
    userId: String,
    authViewModel: AuthViewModel = viewModel()
) {
    // Fetch current user
    val user = authViewModel.currentUser.value

    LaunchedEffect(userId) {
        authViewModel.getCurrentUser(userId)
    }

    // Role-based redirect
    LaunchedEffect(user?.role) {
        if (user?.role == "caregiver") {
            navController.navigate("schedule_screen") {
                popUpTo("index_screen") { inclusive = true }
            }
        }
    }

    if (user == null) {
        Text("Loading...", modifier = Modifier.padding(20.dp))
        return
    }

    // Example appointments list (replace with real repo call)
    val appointments = remember { mutableStateOf(listOf<String>()) } // e.g., list of appointment titles

    val bottomButtons = listOf(
        "View Profile" to {navController.navigate("profile_screen/${user.uid}")},
        "View Messages" to {navController.navigate("inbox/${user.uid}")},
        "Pending Requests" to {navController.navigate("pending_request/${user.uid}")}
    )

    // Layout with top content and bottom buttons
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
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
                    onClick = { navController.navigate("search_appointments") }
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

        // Bottom buttons
        Column {
            bottomButtons.chunked(3).forEach { rowButtons ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowButtons.forEach { (label, action) ->
                        Button(
                            onClick = action,
                            modifier = Modifier
                                .weight(1f) // Equal width per button
                        ) {
                            Text(label)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}