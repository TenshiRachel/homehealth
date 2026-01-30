package com.example.homehealth.screens.appointment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.data.models.Appointment
import com.example.homehealth.fragments.BottomNavBar
import com.example.homehealth.ui.textfield.DateTimePickerTextField
import com.example.homehealth.ui.textfield.TextFieldWithLabel
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScheduleScreen(
    navController: NavHostController,
    caretakerId: String,
    authViewModel: AuthViewModel = viewModel(),
    scheduleViewModel: ScheduleViewModel = viewModel()
) {
    val sessionUser = authViewModel.currentUser.value
    val selectedCaretakerName = scheduleViewModel.selectedCaretakerName.value

    LaunchedEffect(caretakerId) {
        scheduleViewModel.fetchSelectedCaretakerName(caretakerId)
    }

    if (sessionUser == null) {
        Text("Not authenticated")
        return
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var apptDateTime by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    var error by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavBar(navController, sessionUser.uid, sessionUser.role)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Request Appointment with $selectedCaretakerName",
                style = MaterialTheme.typography.headlineMedium
            )

            TextFieldWithLabel(
                label = "Appointment Title",
                value = title,
                onValueChange = { title = it }
            )

            TextFieldWithLabel(
                label = "Description",
                value = description,
                onValueChange = { description = it },
                minLines = 4,
                singleLine = false
            )

            DateTimePickerTextField(
                label = "Appointment Date & Time",
                value = apptDateTime,
                onDateTimeSelected = { apptDateTime = it }
            )

            TextFieldWithLabel(
                label = "My Location",
                value = location,
                onValueChange = { location = it }
            )

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting,
                onClick = {
                    if (title.isBlank() || apptDateTime.isBlank() || location.isBlank()) {
                        error = "Please fill in all required fields"
                        return@Button
                    }

                    isSubmitting = true
                    error = null

                    scheduleViewModel.requestAppointment(
                        Appointment(
                            patientUid = sessionUser.uid,
                            caretakerUid = caretakerId,
                            caretakerName = selectedCaretakerName ?: "",
                            name = title,
                            description = description,
                            bookingDateTime = getFormattedDateTime(),
                            apptDateTime = apptDateTime,
                            location = location,
                            status = "REQUESTED"
                        ),
                        onSuccess = {
                            isSubmitting = false
                            navController.popBackStack()
                        },
                        onError = {
                            isSubmitting = false
                            error = it
                        }
                    )
                }
            ) {
                Text("Send Request")
            }
        }
    }
}

fun getFormattedDateTime(): String {
    return SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
        .format(Date())
}