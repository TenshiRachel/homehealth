package com.example.homehealth.screens.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.AdminViewModel

@Composable
fun AdminDashboardScreen(
    navController: NavHostController,
    adminViewModel: AdminViewModel,
    authViewModel: AuthViewModel = viewModel()
) {
    val admin = adminViewModel.currentAdmin.value
    val sessionUser = authViewModel.currentUser.value

    LaunchedEffect(sessionUser) {
        if (sessionUser != null) {
            adminViewModel.setAdminSession(sessionUser)
        }
    }

    // Role-based redirect
    LaunchedEffect(admin) {
        if (admin != null && admin.role != "admin") {
            navController.navigate("index_screen/${admin.uid}") {
                popUpTo("admin_graph") { inclusive = true }
            }
        }
    }

    if (admin == null) {
        Text("Loading...", modifier = Modifier.padding(20.dp))
        return
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Admin Dashboard", style = MaterialTheme.typography.headlineMedium)
            Text("Welcome, ${admin.name}")

            Spacer(Modifier.height(24.dp))

            Button(onClick = {
                navController.navigate("manage_users_screen")
            }) {
                Text("Manage Users")
            }
        }
    }
}