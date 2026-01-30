package com.example.homehealth.ui.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.homehealth.data.models.User

@Composable
fun CaretakerPreviewCard(
    caretaker: User, // replace with your actual model import
    onViewDetails: () -> Unit,
    onBookAppointment: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        val caretakerDetails = caretaker.caretakerDetails
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Name
            Text(
                text = caretaker.name,
                style = MaterialTheme.typography.titleMedium
            )

            val flexibility = caretakerDetails?.availabilityType?.name?.lowercase()?.replace("_", " ")

            // Preview details
            Text(
                text = "${caretakerDetails?.gender?.name?.lowercase()?.replaceFirstChar { it.uppercase() }} • " +
                        "${caretakerDetails?.age} years old • " +
                        "${caretakerDetails?.caretakerType?.name?.lowercase()?.replace("_", " ")}\n" +
                        "Flexibility: ${if (flexibility == "both") "both weekdays and weekends" else "$flexibility only"}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onViewDetails
                ) {
                    Text("View Details")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onBookAppointment
                ) {
                    Text("Book Appointment")
                }
            }
        }
    }
}