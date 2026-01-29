package com.example.homehealth.screens.admin.certification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.IconButton
import com.example.homehealth.data.models.Certification
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.homehealth.viewmodels.CertificationViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState

@Composable
fun ManageCertificationScreen(
    navController: NavHostController,
    certificationViewModel: CertificationViewModel = viewModel(),
) {
    val certifications by certificationViewModel.certifications.collectAsState()
    val isLoading by certificationViewModel.isLoading
    val errorMessage by certificationViewModel.errorMessage

    var certificationToEdit by remember { mutableStateOf<Certification?>(null) }
    var editedCertificationName by remember { mutableStateOf("") }

    var certificationToDelete by remember { mutableStateOf<Certification?>(null) }

    // Load once
    LaunchedEffect(Unit) {
        certificationViewModel.loadCertifications()
    }

    // Snackbar for errors
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            certificationViewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("create_certification_screen")
                }
            ) {
                Text("+")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Manage Certifications",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                if (certifications.isEmpty()) {
                    Text("No certifications added yet")
                } else {
                    LazyColumn {
                        items(certifications) { certification ->
                            CertificationRow(
                                certification = certification,
                                onEdit = {
                                    certificationToEdit = certification
                                    editedCertificationName = certification.name
                                },
                                onDelete = {
                                    certificationToDelete = certification
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // ✏️ Edit dialog
    certificationToEdit?.let { certification ->
        AlertDialog(
            onDismissRequest = { certificationToEdit = null },
            title = { Text("Edit Certification") },
            text = {
                OutlinedTextField(
                    value = editedCertificationName,
                    onValueChange = { editedCertificationName = it },
                    label = { Text("Certification name") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    enabled = editedCertificationName.isNotBlank(),
                    onClick = {
                        certificationViewModel.updateCertification(
                            certification.copy(name = editedCertificationName)
                        )
                        certificationToEdit = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { certificationToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 🗑 Delete dialog
    certificationToDelete?.let { certification ->
        AlertDialog(
            onDismissRequest = { certificationToDelete = null },
            title = { Text("Delete Certification") },
            text = {
                Text("Are you sure you want to delete \"${certification.name}\"?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        certificationViewModel.deleteCertification(certification.id)
                        certificationToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { certificationToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CertificationRow(
    certification: Certification,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(certification.name)

        Row {
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Certification"
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Certification"
                )
            }
        }
    }
}