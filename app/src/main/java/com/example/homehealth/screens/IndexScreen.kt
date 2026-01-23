package com.example.homehealth.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.data.models.Appointment
import com.example.homehealth.fragments.BottomNavBar
import com.example.homehealth.ui.cards.AppointmentCard
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.IndexViewModel

@Composable
fun IndexScreen(
    navController: NavHostController,
    indexViewModel: IndexViewModel,
    authViewModel: AuthViewModel = viewModel()
) {
    // Get user from session
    val user = indexViewModel.currentUser.value
    val sessionUser = authViewModel.currentUser.value
    val appointments = indexViewModel.appointments.value

    LaunchedEffect(sessionUser) {
        sessionUser?.uid?.let { userId ->
            indexViewModel.getCurrentUser(userId)
        }
    }

    // Role-based redirect
    LaunchedEffect(user) {
        if (user == null) return@LaunchedEffect

        when (user.role) {
            "public" -> Unit

            "caretaker" -> {
                navController.navigate("caretaker_landing/${user.uid}") {
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

    LaunchedEffect(user?.uid) {
        user?.uid?.let {
            indexViewModel.fetchAppointments(it)
        }
    }

    if (user == null) {
        Text("Loading...", modifier = Modifier.padding(20.dp))
        return
    }

    Scaffold (
        bottomBar = { BottomNavBar(navController, user.uid, user.role) }
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
                    text = "Welcome back, ${user.name}!",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // will implement a filter soon to separate booked and completed appointments
                Text(
                    text = "Your Appointments",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (appointments.isEmpty()) {
                    Text("You have no appointments yet.")

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { navController.navigate("browse_caretaker_screen/${user.uid}") }
                    ) {
                        Text("Search Appointments")
                    }
                } else {
                    AppointmentList(
                        userId = user.uid,
                        appointments = appointments,
                        navController = navController,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { navController.navigate("browse_caretaker_screen/${user.uid}") }
                    ) {
                        Text("Search for more appointments")
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentList(
    userId: String,
    appointments: List<Appointment>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(appointments) { appointment ->
            AppointmentCard(
                appointment = appointment,
                onClick = {
                    Log.d("appointmentID", appointment.id)
                    navController.navigate(
                        "appointment_details_screen/${appointment.id}/${userId}"
                    )
                }
            )
        }
    }
}

@Composable
fun AppointmentStatusChip(status: String) {
    val color = when (status.lowercase()) {
        "booked" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    AssistChip(
        onClick = {},
        label = {
            Text(
                text = status.replaceFirstChar { it.uppercase() },
                maxLines = 1
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            labelColor = color
        )
    )
}
