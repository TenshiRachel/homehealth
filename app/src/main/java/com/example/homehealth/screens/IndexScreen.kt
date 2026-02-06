package com.example.homehealth.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
fun IndexScreen(
    navController: NavHostController,
    indexViewModel: IndexViewModel,
    authViewModel: AuthViewModel = viewModel()
) {
    val sessionUser = authViewModel.currentUser.value
    val appointments by indexViewModel.appointments.collectAsState()
    val isLoading by indexViewModel.isLoading.collectAsState()
    val createdChatId by indexViewModel.createdChatId.collectAsState(null)

    // Filter component
    val selectedStatus = remember { mutableStateOf<String?>(null) }
    val statusOptions = listOf("requested", "booked", "completed")
    val filteredAppointments = appointments.filter { appointment ->
        selectedStatus.value == null || appointment.status.equals(selectedStatus.value!!, ignoreCase = true)
    }

    val context = LocalContext.current

    LaunchedEffect(createdChatId) {
        createdChatId?.let { chatId ->
            navController.navigate("chat_screen/$chatId")
            indexViewModel.consumeChatId()
        }
    }

    // Role-based redirect
    LaunchedEffect(sessionUser) {
        if (sessionUser == null) return@LaunchedEffect
        Log.d("Index", "Logged in successfully")

        when (sessionUser.role) {
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
                Log.w("RoleRedirect", "Role not ready yet: ${sessionUser.role}")
                // DO NOTHING — wait for state to settle
            }
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

    LaunchedEffect(sessionUser.uid) {
        indexViewModel.startObservingAppointments(
            recipientUid = sessionUser.uid,
            isCaretaker = false
        )
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

            when {
                isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                appointments.isEmpty() -> {
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

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { navController.navigate("browse_caretaker_screen") }
                        ) {
                            Text("Search Appointments")
                        }
                    }
                }

                else -> {
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
                        sessionUser = sessionUser!!,
                        appointments = filteredAppointments,
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
}
