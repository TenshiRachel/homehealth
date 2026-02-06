package com.example.homehealth.screens.appointment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.fragments.BottomNavBar
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.ScheduleViewModel

@Composable
fun AppointmentDetailsScreen(
    navController: NavHostController,
    appointmentId: String,
    authViewModel: AuthViewModel = viewModel(),
    scheduleViewModel: ScheduleViewModel = viewModel()
) {
    val sessionUser = authViewModel.currentUser.value

    val appointment by scheduleViewModel.currentAppointment.collectAsState()

    // Dialog states
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    if (sessionUser == null) {
        Text("Not authenticated")
        return
    }

    // Fetch appointment on load
    LaunchedEffect(appointmentId) {
        //scheduleViewModel.fetchAppointmentDetails(appointmentId)
        scheduleViewModel.observeAppointment(appointmentId)
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController, sessionUser.uid, sessionUser.role) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Appointment Details",
                style = MaterialTheme.typography.headlineMedium
            )

            if (appointment == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                return@Column
            }

            // Description card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = appointment!!.name, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = appointment!!.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Details card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailRow(Icons.Default.Person, "Caretaker", appointment!!.caretakerName)
                    DetailRow(Icons.Default.Badge, "Caretaker ID", appointment!!.caretakerUid)
                    DetailRow(Icons.Default.CalendarToday, "Booking Date & Time", appointment!!.bookingDateTime)
                    DetailRow(Icons.Default.Schedule, "Appointment Date & Time", appointment!!.apptDateTime)
                    DetailRow(Icons.Default.Place, "Location", appointment!!.location)
                    DetailRow(Icons.Default.Info, "Appointment Status", appointment!!.status)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Button logic based on role
            if (sessionUser.role == "public") {
                val isBooked = appointment!!.status.equals("BOOKED", ignoreCase = true)
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isBooked,
                    onClick = { showConfirmDialog = true }
                ) { Text("Mark as Completed") }
            } else if (sessionUser.role == "caretaker") {
                val isRequested = appointment!!.status.equals("REQUESTED", ignoreCase = true)
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isRequested,
                    onClick = { showConfirmDialog = true }
                ) { Text("Approve Booking") }
            }

            // Confirm Dialog
            if (showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    title = {
                        Text(
                            if (sessionUser.role == "public") "Confirm Completion" else "Confirm Booking"
                        )
                    },
                    text = {
                        Text(
                            if (sessionUser.role == "public")
                                "Are you sure this appointment has been completed?"
                            else
                                "Continue with the booking?"
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showConfirmDialog = false
                                if (sessionUser.role == "public") {
                                    scheduleViewModel.markAppointmentCompleted(appointment!!.id)
                                } else {
                                    scheduleViewModel.markAppointmentBooked(appointment!!.id)
                                }
                                showSuccessDialog = true
                            }
                        ) { Text("Yes") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Success Dialog
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    title = {
                        Text(
                            if (sessionUser.role == "public") "Appointment Completed" else "Booking Confirmed"
                        )
                    },
                    text = {
                        Text(
                            if (sessionUser.role == "public")
                                "This appointment has been successfully marked as completed. Please proceed to pay your caretaker."
                            else
                                "This appointment has been successfully marked as booked. Make sufficient preparation for your patient's well-being."
                        )
                    },
                    confirmButton = {
                        Button(onClick = { showSuccessDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}