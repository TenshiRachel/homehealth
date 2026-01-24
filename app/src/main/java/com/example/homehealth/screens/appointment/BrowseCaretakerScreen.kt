package com.example.homehealth.screens.appointment

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.fragments.BottomNavBar
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.ScheduleViewModel

@Composable
fun BrowseCaretakerScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    scheduleViewModel: ScheduleViewModel = viewModel()
) {
    val sessionUser = authViewModel.currentUser.value
    val caretakers = scheduleViewModel.caretakers.value

    // auth guard
    if (sessionUser == null) {
        Text("Not authenticated")
        return
    }

    // Fetch caretakers once
    LaunchedEffect(Unit) {
        scheduleViewModel.fetchAvailableCaretakers()
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController,
                sessionUser.uid,
                sessionUser.role
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Text(
                text = "Available Caretakers",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (caretakers != null) {
                caretakers.forEach { caretaker ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = caretaker.name,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Button(
                            onClick = {
                                navController.navigate(
                                    "schedule_screen/${caretaker.uid}"
                                )
                            }
                        ) {
                            Text("Book Appointment")
                        }
                    }
                }
            }
            else{
                Text("No caretakers available at the moment.")
            }
        }
    }
}
