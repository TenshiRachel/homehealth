package com.example.homehealth.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.input.KeyboardType
import com.example.homehealth.data.models.CaretakerDetails
import com.example.homehealth.data.models.Certification
import com.example.homehealth.data.enums.Gender
import com.example.homehealth.ui.textfield.EnumDropdownField
import com.example.homehealth.ui.textfield.ItemDropdownField
import com.example.homehealth.ui.textfield.TextField2

@Composable
fun CreateCaretakerScreen(
    navController: NavHostController,
    adminViewModel: AdminViewModel = viewModel(),
    certificationViewModel: CertificationViewModel = viewModel()
) {
    val DEFAULT_PASSWORD = "password123"

    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var yearsOfExp by remember { mutableStateOf("") }

    var selectedGender by remember { mutableStateOf(Gender.UNSPECIFIED) }

    var selectedCert by remember { mutableStateOf<Certification?>(null) }

    val certifications by certificationViewModel.certifications.collectAsState()
    val isLoading by adminViewModel.isLoading
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }

    val pdfPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            selectedPdfUri = uri
        }

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

        TextField2(
            label = "Name",
            value = name,
            onValueChange = { name = it }
        )

        TextField2(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        TextField2(
            label = "Age",
            value = age,
            onValueChange = { if (it.all(Char::isDigit)) age = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        EnumDropdownField(
            label = "Gender",
            selectedValue = selectedGender,
            values = Gender.values(),
            exclude = { it == Gender.UNSPECIFIED },
            onValueSelected = { selectedGender = it }
        )

        TextField2(
            label = "Years of Experience",
            value = yearsOfExp,
            onValueChange = { if (it.all(Char::isDigit)) yearsOfExp = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Text("Certifications", style = MaterialTheme.typography.titleMedium)
        ItemDropdownField(
            label = "Certification",
            items = certifications,
            selectedItem = selectedCert,
            onItemSelected = { selectedCert = it },
            itemLabel = { it.name }
        )
        Button(
            onClick = {
                pdfPickerLauncher.launch(arrayOf("application/pdf"))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (selectedPdfUri == null)
                    "Select Certification PDF"
                else
                    "PDF Selected: ${selectedPdfUri!!.lastPathSegment}"
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            onClick = {
                if (
                    name.isBlank() ||
                    email.isBlank() ||
                    selectedGender == Gender.UNSPECIFIED ||
                    age.isBlank() ||
                    yearsOfExp.isBlank() ||
                    selectedCert == null
                ) {
                    // show error: "Please fill in all fields"
                    return@Button
                }

                val details = CaretakerDetails(
                    gender = selectedGender,
                    age = age.toIntOrNull() ?: 0,
                    yearsOfExperience = yearsOfExp.toIntOrNull() ?: 0,
                    certificationIds = listOf(selectedCert!!.id)
                )

                adminViewModel.createCaretakerAccount(
                    email = email,
                    password = DEFAULT_PASSWORD,
                    name = name,
                    details = details,
                    certPdfUri = selectedPdfUri,
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