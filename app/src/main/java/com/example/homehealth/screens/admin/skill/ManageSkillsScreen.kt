package com.example.homehealth.screens.admin.skill

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
import com.example.homehealth.viewmodels.SkillViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.homehealth.data.models.Skill
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun ManageSkillsScreen(
    navController: NavHostController,
    skillViewModel: SkillViewModel = viewModel(),
) {
    val skills by skillViewModel.skills
    val isLoading by skillViewModel.isLoading

    var skillToEdit by remember { mutableStateOf<Skill?>(null) }
    var editedSkillName by remember { mutableStateOf("") }

    var skillToDelete by remember { mutableStateOf<Skill?>(null) }

    // Load once
    LaunchedEffect(Unit) {
        skillViewModel.loadSkills()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("create_skill_screen")
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
                text = "Manage Skills",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(skills) { skill ->
                        SkillRow(
                            skill = skill,
                            onEdit = {
                                skillToEdit = skill
                                editedSkillName = skill.name

                            },
                            onDelete = {
                                skillToDelete = skill
                            }
                        )
                    }
                }

                skillToEdit?.let { skill ->
                    AlertDialog(
                        onDismissRequest = { skillToEdit = null },
                        title = { Text("Edit Skill") },
                        text = {
                            OutlinedTextField(
                                value = editedSkillName,
                                onValueChange = { editedSkillName = it },
                                label = { Text("Skill name") },
                                singleLine = true
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    skillViewModel.updateSkill(
                                        skill.copy(name = editedSkillName)
                                    )
                                    skillToEdit = null
                                }
                            ) {
                                Text("Save")
                            }
                        },
                        dismissButton = {
                            OutlinedButton(onClick = { skillToEdit = null }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                skillToDelete?.let { skill ->
                    AlertDialog(
                        onDismissRequest = { skillToDelete = null },
                        title = { Text("Delete Skill") },
                        text = {
                            Text("Are you sure you want to delete \"${skill.name}\"?")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    skillViewModel.deleteSkill(skill.id)
                                    skillToDelete = null
                                }
                            ) {
                                Text("Delete")
                            }
                        },
                        dismissButton = {
                            OutlinedButton(onClick = { skillToDelete = null }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SkillRow(
    skill: Skill,
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
        Text(skill.name)

        Row {
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Skill"
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Skill"
                )
            }
        }
    }
}