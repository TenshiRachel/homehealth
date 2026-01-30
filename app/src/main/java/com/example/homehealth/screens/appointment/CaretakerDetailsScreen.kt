package com.example.homehealth.screens.appointment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.data.models.Appointment
import com.example.homehealth.data.models.CaretakerProfile
import com.example.homehealth.fragments.BottomNavBar
import com.example.homehealth.screens.profile.CaretakerProfileContent
import com.example.homehealth.ui.textfield.DateTimePickerTextField
import com.example.homehealth.ui.textfield.TextFieldWithLabel
import com.example.homehealth.utils.display
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.CaretakerViewModel
import com.example.homehealth.viewmodels.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.ifEmpty

@Composable
fun CaretakerDetailsScreen(
    navController: NavHostController,
    caretakerId: String,
    authViewModel: AuthViewModel = viewModel(),
    caretakerViewModel: CaretakerViewModel = viewModel()
) {
    val sessionUser = authViewModel.currentUser.value
    val loadedCaretakerDetails by caretakerViewModel.caretakerProfile.collectAsState()
    val isLoading by caretakerViewModel.isLoading.collectAsState()
    val error by caretakerViewModel.error.collectAsState()

    LaunchedEffect(caretakerId) {
        caretakerViewModel.loadCaretakerProfile(caretakerId)
    }

    if (sessionUser == null || loadedCaretakerDetails == null) {
        Text("Not authenticated")
        CircularProgressIndicator()
        return
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController, sessionUser.uid, sessionUser.role) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            sessionUser.uid.let { caretakerViewModel.loadCaretakerProfile(it) }
                        }) {
                            Text("Retry")
                        }
                    }
                }
                loadedCaretakerDetails != null -> {
                    CaretakerProfileContent(profile = loadedCaretakerDetails!!)
                }
            }
        }
    }
}

@Composable
fun CaretakerProfileContent(profile: CaretakerProfile) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header Section
        Text(
            text = profile.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = profile.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bio
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("About", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = profile.bio.ifEmpty { "No bio provided" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (profile.bio.isEmpty())
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Details Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Details", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                ProfileDetailRow("Gender", profile.gender.display())
                ProfileDetailRow("Age",
                    if (profile.age > 0) "${profile.age} years" else "Not specified")
                ProfileDetailRow("Experience", if (profile.yearsOfExperience > 0)
                    "${profile.yearsOfExperience} years" else "Not specified")
                ProfileDetailRow("Type", profile.caretakerType.display())
                ProfileDetailRow("Availability", profile.availabilityType.display())
                ProfileDetailRow("Night Care", if (profile.nightCare) "Available" else "Not Available")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Skills Section
        if (profile.skills.isEmpty()) {
            Text(
                "No skills added yet",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Skills", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        profile.skills.forEach { skill ->
                            AssistChip(
                                onClick = { },
                                label = { Text(skill.name) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Certifications Section
        if (profile.certifications.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Certifications", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    profile.certifications.forEach { cert ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(cert.name, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}