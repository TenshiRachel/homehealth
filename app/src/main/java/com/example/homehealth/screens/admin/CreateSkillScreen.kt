package com.example.homehealth.screens.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.viewmodels.SkillViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun CreateSkillScreen(
    navController: NavHostController,
    skillViewModel: SkillViewModel = viewModel(),
) {
    var skillName by remember { mutableStateOf("") }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Create Skill", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = skillName,
                onValueChange = { skillName = it },
                label = { Text("Skill name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = skillName.isNotBlank(),
                onClick = {
                    skillViewModel.createSkill(skillName)
                    navController.popBackStack()
                }
            ) {
                Text("Save")
            }
        }
    }
}
