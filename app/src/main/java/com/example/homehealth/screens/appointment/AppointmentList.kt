package com.example.homehealth.screens.appointment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.homehealth.data.models.Appointment
import com.example.homehealth.data.models.User
import com.example.homehealth.ui.cards.AppointmentCard
import com.example.homehealth.viewmodels.IndexViewModel

@Composable
fun AppointmentList(
    indexViewModel: IndexViewModel,
    sessionUser: User,
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
                onViewDetails = {
//                    Log.d("appointmentID", appointment.id)
                    navController.navigate(
                        "appointment_details_screen/${appointment.id}"
                    )
                },
                onChat = {
                    // Create chat
                    indexViewModel.createChat(
                        currentUserId = sessionUser.uid,
                        userName1 = sessionUser.name,
                        userId2 = appointment.caretakerUid,
                        userName2 = appointment.caretakerName
                    )
                },
            )
        }
    }
}

@Composable
fun AppointmentStatusChip(status: String) {
    val color = when (status.lowercase()) {
        "requested" -> MaterialTheme.colorScheme.outline
        "booked" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.inversePrimary
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