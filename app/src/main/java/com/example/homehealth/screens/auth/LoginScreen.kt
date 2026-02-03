package com.example.homehealth.screens.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AlertDialog
import androidx.navigation.NavHostController
import com.example.homehealth.viewmodels.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homehealth.data.models.User

@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
){
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPasswordResetDialog by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf<User?>(null) }

    // Show password reset dialog if needed
    if (showPasswordResetDialog && currentUser != null) {
        PasswordResetDialog(
            onPasswordReset = { newPassword ->
                authViewModel.resetPassword(
                    newPassword = newPassword,
                    userId = currentUser!!.uid
                ) { success, message ->
                    if (success) {
                        showPasswordResetDialog = false
                        Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                        // Navigate to appropriate screen
                        when (currentUser!!.role) {
                            "admin" -> {
                                navController.navigate("admin_graph") {
                                    popUpTo("login_screen") { inclusive = true }
                                }
                            }
                            "public", "caretaker" -> {
                                navController.navigate("index_screen") {
                                    popUpTo("login_screen") { inclusive = true }
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, message ?: "Failed to update password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Login",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(value = email, onValueChange = { email = it },
            label = { Text("Email") }, singleLine = true)

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            authViewModel.login(email, password, context) { isSuccess, message, user ->
                if (isSuccess && user != null) {
                    Log.e("LoginScreen", "User logged in: ${user.email}")
                    Log.e("LoginScreen", "User role: ${user.role}")
                    Log.e("LoginScreen", "Requires password reset: ${user.requiresPasswordReset}")

                    // Check if password reset is required FIRST
                    if (user.requiresPasswordReset) {
                        Log.d("LoginScreen", "Showing password reset dialog")
                        currentUser = user
                        showPasswordResetDialog = true
                        return@login  // Exit here - don't navigate
                    }

                    when (user.role) {
                        "admin" -> {
                            Log.d("Login", "Admin login success, User ID: ${user.uid}")
                            navController.navigate("admin_graph") {
                                popUpTo("login_screen") { inclusive = true }
                            }
                        }
                        "public", "caretaker" -> {
                            Log.d("Login", "${user.role} login success, User ID: ${user.uid}")
                            navController.navigate("index_screen") {
                                popUpTo("login_screen") { inclusive = true }
                            }
                        }
                        else -> {
                            authViewModel.logout()
                            Toast.makeText(context, "Unauthorized role", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Login failed: $message", Toast.LENGTH_LONG).show()
                    Log.e("LoginScreen", "Login failed: $message")
                }
            }
        },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank()
        ) {
            Text("Login")
        }

        TextButton(
            onClick = {navController.navigate("register_screen")},
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Don't have an account? Register here")
        }
    }
}

@Composable
fun PasswordResetDialog(
    onDismiss: () -> Unit = {},
    onPasswordReset: (String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { /* Cannot dismiss - force password reset */ },
        title = { Text("Reset Your Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "You're using a default password. Please set a new password to continue.",
                    style = MaterialTheme.typography.bodyMedium
                )

                TextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        errorMessage = null
                    },
                    label = { Text("New Password") },
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible)
                                    "Hide password"
                                else
                                    "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = null
                    },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        newPassword.length < 6 -> {
                            errorMessage = "Password must be at least 6 characters"
                        }
                        newPassword != confirmPassword -> {
                            errorMessage = "Passwords don't match"
                        }
                        else -> {
                            onPasswordReset(newPassword)
                        }
                    }
                },
                enabled = newPassword.isNotBlank() && confirmPassword.isNotBlank()
            ) {
                Text("Reset Password")
            }
        }
    )
}