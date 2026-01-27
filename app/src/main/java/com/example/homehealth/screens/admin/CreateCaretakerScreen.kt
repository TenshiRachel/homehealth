package com.example.homehealth.screens.admin

import androidx.navigation.NavHostController
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import com.example.homehealth.viewmodels.AdminViewModel
import com.example.homehealth.viewmodels.CertificationViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import com.example.homehealth.data.models.CaretakerDetails
import com.example.homehealth.data.models.Certification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCaretakerScreen(
    navController: NavHostController,
    adminViewModel: AdminViewModel = viewModel(),
    certificationViewModel: CertificationViewModel = viewModel()
) {
    val DEFAULT_PASSWORD = "password123"

    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var yearsOfExp by remember { mutableStateOf("") }
    var selectedCert by remember { mutableStateOf<Certification?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val certifications by certificationViewModel.certifications
    val isLoading by adminViewModel.isLoading

    LaunchedEffect(Unit) {
        certificationViewModel.loadCertifications()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Create Caretaker Account", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            visualTransformation = PasswordVisualTransformation(),
//            placeholder = { Text(DEFAULT_PASSWORD) },
//            modifier = Modifier.fillMaxWidth()
//        )

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = gender,
            onValueChange = { gender = it },
            label = { Text("Gender") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = yearsOfExp,
            onValueChange = { yearsOfExp = it },
            label = { Text("Years of Experience") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Certifications", style = MaterialTheme.typography.titleMedium)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCert?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Certification") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                certifications.forEach { cert ->
                    DropdownMenuItem(
                        text = { Text(cert.name) },
                        onClick = {
                            selectedCert = cert
                            expanded = false
                        }
                    )
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            onClick = {
                if (
                    name.isBlank() ||
                    email.isBlank() ||
                    gender.isBlank() ||
                    age.isBlank() ||
                    yearsOfExp.isBlank() ||
                    selectedCert == null
                ) {
                    // show error: "Please fill in all fields"
                    return@Button
                }

                val details = CaretakerDetails(
                    gender = gender,
                    age = age.toIntOrNull() ?: 0,
                    yearsOfExperience = yearsOfExp.toIntOrNull() ?: 0,
                    certificationIds = selectedCert?.let { listOf(it.id) } ?: emptyList()
                )

                adminViewModel.createCaretakerAccount(
                    email = email,
                    password = DEFAULT_PASSWORD,
                    name = name,
                    details = details
                ) { success, _ ->
                    if (success) {
                        navController.popBackStack()
                    }
                }
            }
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Create Caretaker")
            }
        }
    }
}