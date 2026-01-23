package com.example.homehealth.screens.appointment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.data.models.User
import com.example.homehealth.fragments.BottomNavBar
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.IndexViewModel
import com.example.homehealth.viewmodels.ScheduleViewModel

@Composable
fun AppointmentDetailsScreen(
    navController: NavHostController,
    appointmentId: String,
    authViewModel: AuthViewModel = viewModel(),
    scheduleViewModel: ScheduleViewModel = viewModel()
) {
    val user by scheduleViewModel.currentUser.observeAsState()

    LaunchedEffect(appointmentId) {
        scheduleViewModel.fetchAppointmentDetails(appointmentId)
    }

//    val caretakers by scheduleViewModel.caretakers.observeAsState(emptyList())

//    LaunchedEffect(userId) {
//        scheduleViewModel.fetchCurrentUser(userId)
//    }

//    // auth guard
//    if (user == null) {
//        Text("Not authenticated")
//        return
//    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController,
                user!!.uid,
                user!!.role
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Text(
                text = "Appointment Details Screen for $appointmentId - TBD",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
