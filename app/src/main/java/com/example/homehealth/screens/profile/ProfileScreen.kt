package com.example.homehealth.screens.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    userId: String,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val user by profileViewModel.profileUser

    LaunchedEffect(userId) {
        profileViewModel.loadProfile(userId)
    }

    user?.let { profile ->
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                navController.navigate("edit_profile_screen/${profile.uid}")
            },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Profile")
            }
        }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading profile...")
        }
    }
}