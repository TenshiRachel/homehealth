package com.example.homehealth.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.fragments.BottomNavBar
import com.example.homehealth.screens.appointment.AppointmentList
import com.example.homehealth.utils.FilterChipsRow
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.IndexViewModel

@Composable
fun CaretakerLandingScreen(
    navController: NavHostController,
    indexViewModel: IndexViewModel,
    authViewModel: AuthViewModel = viewModel()
) {
    // Get user from session
    val sessionUser = authViewModel.currentUser.value
    val appointments by indexViewModel.caretakerAppointments.observeAsState(emptyList())

    val selectedStatus = remember { mutableStateOf<String?>(null) }
    val statusOptions = listOf("requested", "booked", "completed")
    val filteredAppointments = appointments.filter { appointment ->
        selectedStatus.value == null || appointment.status.equals(selectedStatus.value!!, ignoreCase = true)
    }

    val createdChatId by indexViewModel.createdChatId.collectAsState(null)

    LaunchedEffect(createdChatId) {
        createdChatId?.let { chatId ->
            navController.navigate("chat_screen/$chatId")
            indexViewModel.consumeChatId()
        }
    }

    LaunchedEffect(sessionUser) {
        if (sessionUser == null) return@LaunchedEffect
        else {
            indexViewModel.fetchAppointmentsByCaretaker(sessionUser.uid)
        }
    }


    if (sessionUser == null) {
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
        bottomBar = { BottomNavBar(navController, sessionUser.uid, sessionUser.role) }
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
                text = "Welcome back, ${sessionUser.name}!",
                style = MaterialTheme.typography.headlineMedium
            )

            if (appointments.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "You have no appointments yet.",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Check back later.",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your Appointments",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                FilterChipsRow(
                    title = "",
                    options = statusOptions,
                    selected = selectedStatus.value,
                    onSelectedChange = { selectedStatus.value = it },
                    label = { it.replaceFirstChar { c -> c.uppercase() } }
                )

                Spacer(modifier = Modifier.height(8.dp))

                AppointmentList(
                    indexViewModel = indexViewModel,
                    sessionUser = sessionUser,
                    appointments = filteredAppointments,
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
