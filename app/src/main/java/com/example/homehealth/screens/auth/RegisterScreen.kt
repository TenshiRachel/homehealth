package com.example.homehealth.screens.auth

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.homehealth.viewmodels.AuthViewModel
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun RegisterScreen(navController: NavHostController){
    val context = LocalContext.current
    val authViewModel = AuthViewModel()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Register",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Pending implementation to remove emojis from all fields
        OutlinedTextField(value = username, onValueChange = { username = it },
            label = { Text("Name") }, singleLine = true
        )
        OutlinedTextField(value = email, onValueChange =  { email = it },
            label = { Text("Email") }, singleLine = true
        )
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

        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            authViewModel.register(username, email, password, confirm) { success, message ->
                if (success){
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    navController.navigate("login_screen")
                }
                else {
                    message?.let{
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        },
            modifier = Modifier.fillMaxWidth(),
            enabled = username.isNotBlank() && email.isNotBlank() && password.isNotBlank()
        ) {
            Text("Register")
        }

        TextButton(
            onClick = {navController.navigate("login_screen")},
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Already have an account? Please log in")
        }
    }
}