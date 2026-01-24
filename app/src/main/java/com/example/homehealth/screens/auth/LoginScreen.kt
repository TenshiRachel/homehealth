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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.homehealth.viewmodels.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
){
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            authViewModel.loginFirebase(email, password) { success, message, user ->
                if (success && user != null) {
                    when (user.role){
                        "public" -> {
                            navController.navigate("index_screen"){
                                Log.d("Login", "Success, User ID: $message")
                                popUpTo("login_screen") {inclusive}
                            }
                        }
                        "caretaker" -> {
                            Log.d("Login", "Success, User ID: $message")
                            navController.navigate("index_screen"){
                                popUpTo("login_screen") {inclusive}
                            }
                        }
                        "admin" -> {
                            Log.d("Login", "Success, User ID: $message")
                            navController.navigate("admin_graph"){
                                popUpTo("login_screen") {inclusive}
                            }
                        }
                        else -> {
                            authViewModel.logout()
                            Toast.makeText(
                                context,
                                "Unauthorized role",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    message?.let{
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
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