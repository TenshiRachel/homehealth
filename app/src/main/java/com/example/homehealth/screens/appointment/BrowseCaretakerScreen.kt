package com.example.homehealth.screens.appointment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.data.enums.AvailabilityType
import com.example.homehealth.data.enums.CaretakerType
import com.example.homehealth.fragments.BottomNavBar
import com.example.homehealth.ui.cards.CaretakerPreviewCard
import com.example.homehealth.utils.FilterChipsRow
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.ScheduleViewModel

@Composable
fun BrowseCaretakerScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    scheduleViewModel: ScheduleViewModel = viewModel()
) {
    val sessionUser = authViewModel.currentUser.value
    val caretakers = scheduleViewModel.caretakers.value ?: emptyList()

    // filter component
    val availabilityOptions = listOf(AvailabilityType.BOTH, AvailabilityType.WEEKDAYS,
        AvailabilityType.WEEKENDS)
    val categoryOptions = listOf(CaretakerType.FULL_TIME, CaretakerType.PART_TIME)
    val selectedAvailability = remember { mutableStateOf<AvailabilityType?>(null) }
    val selectedCategory = remember { mutableStateOf<CaretakerType?>(null) }

    if (sessionUser == null) {
        Text("Not authenticated")
        return
    }

    LaunchedEffect(Unit) {
        scheduleViewModel.fetchAvailableCaretakers()
    }

    val filteredCaretakers = caretakers.filter { caretaker ->
        val details = caretaker.caretakerDetails

        val availabilityMatches =
            selectedAvailability.value == null ||
                    details?.availabilityType == selectedAvailability.value

        val categoryMatches =
            selectedCategory.value == null ||
                    details?.caretakerType == selectedCategory.value

        availabilityMatches && categoryMatches
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(navController, sessionUser.uid, sessionUser.role)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Text(
                text = "Browse Caretakers",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilterChipsRow(
                title = "Availability:",
                options = availabilityOptions,
                selected = selectedAvailability.value,
                onSelectedChange = { selectedAvailability.value = it },
                label = { availability ->
                    when (availability) {
                        AvailabilityType.BOTH -> "Both"
                        AvailabilityType.WEEKDAYS -> "Weekdays"
                        AvailabilityType.WEEKENDS -> "Weekends"
                        else -> ""
                    }
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            FilterChipsRow(
                title = "Type:",
                options = categoryOptions,
                selected = selectedCategory.value,
                onSelectedChange = { selectedCategory.value = it },
                label = { category ->
                    when (category) {
                        CaretakerType.FULL_TIME -> "Full-Time"
                        CaretakerType.PART_TIME -> "Part-Time"
                        else -> ""
                    }
                }
            )

            if (caretakers.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No caretakers available at the moment.",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredCaretakers) { caretaker ->
                        CaretakerPreviewCard(
                            caretaker = caretaker,
                            onViewDetails = {
                                navController.navigate(
                                    "caretaker_details_screen/${caretaker.uid}"
                                )
                            },
                            onBookAppointment = {
                                navController.navigate(
                                    "schedule_screen/${caretaker.uid}"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
