package com.example.homehealth.screens.profile

import android.net.Uri
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.fragments.BottomNavBar
import com.example.homehealth.viewmodels.AuthViewModel
import com.example.homehealth.viewmodels.ProfileViewModel
import java.io.File

@Composable
fun EditProfileScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel = viewModel()
) {
    val user by profileViewModel.profileUser
    val sessionUser = authViewModel.currentUser.value
    val editState by profileViewModel.editState.collectAsState()

    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                tempPhotoUri?.let { uri ->
                    imageUri = uri
                }
            }
        }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val file = File(context.cacheDir, "profile_image.jpg")
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                tempPhotoUri = uri
                cameraLauncher.launch(uri)
            }
        }

    LaunchedEffect(sessionUser) {
        sessionUser?.uid?.let { userId ->
            profileViewModel.loadProfile(userId)
        }
    }

    if (user == null || sessionUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController, sessionUser.uid, sessionUser.role) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.CAMERA
                            ) -> {
                                val file = File(context.cacheDir, "profile_image.jpg")
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    file
                                )
                                tempPhotoUri = uri
                                cameraLauncher.launch(uri)
                            }
                            else -> {
                                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile picture",
                        tint = Color.Gray,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap to change photo",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = editState.name,
                onValueChange = { profileViewModel.onNameChanged(it) },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = user!!.email,
                onValueChange = {},
                readOnly = true,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = editState.bio,
                onValueChange = { profileViewModel.onBioChanged(it) },
                label = { Text("Bio") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    profileViewModel.saveProfile()
                    navController.popBackStack()
                }
            ) {
                Text("Save")
            }
        }
    }
}

