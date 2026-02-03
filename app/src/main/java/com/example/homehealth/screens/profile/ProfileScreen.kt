package com.example.homehealth.screens.profile

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.fragments.BottomNavBar
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val user by profileViewModel.profileUser
    val sessionUser = authViewModel.currentUser.value

    LaunchedEffect(sessionUser) {
        sessionUser?.uid?.let { userId ->
            profileViewModel.loadProfile(userId)
        }
    }

    if (sessionUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    user?.let { profile ->
        Scaffold (
            bottomBar = { BottomNavBar(navController, sessionUser.uid, sessionUser.role) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // 🔵 Profile picture placeholder
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile picture",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = profile.name,
                    onValueChange = {},
                    label = { Text("Name") },
                    singleLine = true,
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = profile.email,
                    onValueChange = {},
                    label = { Text("Email") },
                    singleLine = true,
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = profile.bio.ifEmpty { "No bio provided" },
                    onValueChange = {},
                    label = { Text("Bio") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = {
                    Log.d("ProfileScreen", "Edit Profile button clicked")
                    // Navigate to EditProfileScreen
                    navController.navigate("edit_profile_screen")
                },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Profile")
                }

                // Logout Button
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        Log.d("ProfileScreen", "Logout button clicked")

                        authViewModel.logout()

                        navController.navigate("login_screen") {
                            popUpTo("auth_graph") {
                                inclusive = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout")
                }
            }
        }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}