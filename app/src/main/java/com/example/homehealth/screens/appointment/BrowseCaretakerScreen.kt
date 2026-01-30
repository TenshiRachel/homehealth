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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.fragments.BottomNavBar
import com.example.homehealth.ui.cards.CaretakerPreviewCard
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.ScheduleViewModel

@Composable
fun BrowseCaretakerScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    scheduleViewModel: ScheduleViewModel = viewModel()
) {
    val sessionUser = authViewModel.currentUser.value
    val caretakers = scheduleViewModel.caretakers.value

    if (sessionUser == null) {
        Text("Not authenticated")
        return
    }

    LaunchedEffect(Unit) {
        scheduleViewModel.fetchAvailableCaretakers()
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

            if (caretakers.isNullOrEmpty()) {
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
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(caretakers) { caretaker ->
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
