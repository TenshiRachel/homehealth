package com.example.homehealth.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.data.models.Appointment
import com.example.homehealth.data.models.User
import com.example.homehealth.fragments.BottomNavBar
import com.example.homehealth.screens.appointment.AppointmentList
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

    val createdChatId by indexViewModel.createdChatId.collectAsState(null)

    LaunchedEffect(createdChatId) {
        createdChatId?.let { chatId ->
            navController.navigate("chat_screen/$chatId")
            indexViewModel.consumeChatId()
        }
    }

    LaunchedEffect(sessionUser) {
        sessionUser?.uid?.let { userId ->
            indexViewModel.getCurrentUser(userId)
        }
    }

    // Role-based redirect
    LaunchedEffect(user) {
        if (user == null) return@LaunchedEffect
        Log.d("Index", "Logged in successfully")

        when (user.role) {
            "public" -> Unit

            "caretaker" -> {
                navController.navigate("caretaker_landing_screen") {
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
            indexViewModel.fetchAppointmentsByPatient(it)
        }
    }

    if (user == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Loading...")
            Spacer(modifier = Modifier.height(12.dp))
            CircularProgressIndicator()
        }
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
                    onClick = { navController.navigate("browse_caretaker_screen") }
                ) {
                    Text("Search Appointments")
                }
            } else {
                AppointmentList(
                    indexViewModel = indexViewModel,
                    sessionUser = sessionUser!!,
                    appointments = appointments,
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    onClick = { navController.navigate("browse_caretaker_screen") }
                ) {
                    Text("Search for more appointments")
                }
            }
        }
    }
}
