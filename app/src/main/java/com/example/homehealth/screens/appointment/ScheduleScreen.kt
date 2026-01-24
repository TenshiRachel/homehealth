package com.example.homehealth.screens.appointment

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.data.models.Appointment
import com.example.homehealth.fragments.BottomNavBar
import com.example.homehealth.ui.textfield.TextFieldWithLabel
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    // auth guard
    if (sessionUser == null) {
        Text("Not authenticated")
        return
    }

    // Form state
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var apptDateTime by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    var error by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

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
                onValueChange = { description = it }
            )

            TextFieldWithLabel(
                label = "Appointment Date & Time",
                value = apptDateTime,
                onValueChange = { apptDateTime = it }
            )

            TextFieldWithLabel(
                label = "My Location",
                value = location,
                onValueChange = { location = it }
            )

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting,
                onClick = {
                    if (
                        title.isBlank() ||
                        apptDateTime.isBlank() ||
                        location.isBlank()
                    ) {
                        error = "Please fill in all required fields"
                        return@Button
                    }

                    isSubmitting = true
                    error = null

                    scheduleViewModel.requestAppointment(
                        Appointment(
                            patientUid = sessionUser.uid,
                            caretakerUid = caretakerId,
                            caretakerName = selectedCaretakerName!!, // optional, can be fetched
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
    val formatter = SimpleDateFormat("dd/MM/yy, HH:mm:ss", Locale.getDefault())
    return formatter.format(Date())
}