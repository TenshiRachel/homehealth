package com.example.homehealth.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homehealth.data.enums.AvailabilityType
import com.example.homehealth.data.enums.CaretakerType
import com.example.homehealth.ui.textfield.EnumDropdownField
import com.example.homehealth.ui.textfield.ItemDropdownField
import com.example.homehealth.ui.textfield.TextField2
import com.example.homehealth.viewmodels.SkillViewModel
import com.example.homehealth.viewmodels.CaretakerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCaretakerProfileScreen(
    navController: NavHostController,
    caretakerProfileViewModel: CaretakerViewModel = viewModel(),
    skillViewModel: SkillViewModel = viewModel()
) {
    val profile by caretakerProfileViewModel.caretakerProfile.collectAsState()
    val isSaving by caretakerProfileViewModel.isSaving.collectAsState()
    val error by caretakerProfileViewModel.error.collectAsState()
    val skills by skillViewModel.skills.collectAsState()

    // Load skills once
    LaunchedEffect(Unit) {
        skillViewModel.loadSkills()
    }

    if (profile == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // --- Local editable state ---
    var bio by remember(profile) { mutableStateOf(profile!!.bio) }
    var caretakerType by remember(profile) { mutableStateOf(profile!!.caretakerType) }
    var availabilityType by remember(profile) { mutableStateOf(profile!!.availabilityType) }
    var nightCare by remember(profile) { mutableStateOf(profile!!.nightCare) }
    var selectedSkill by remember(profile) {
        mutableStateOf(profile!!.skills.firstOrNull())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Bio
            TextField2(
                label = "Bio",
                value = bio,
                onValueChange = { bio = it }
            )

            // Caretaker Type
            EnumDropdownField(
                label = "Caretaker Type",
                selectedValue = caretakerType,
                values = CaretakerType.values(),
                exclude = { it == CaretakerType.UNKNOWN },
                onValueSelected = { caretakerType = it }
            )

            // Availability
            EnumDropdownField(
                label = "Availability",
                selectedValue = availabilityType,
                values = AvailabilityType.values(),
                exclude = { it == AvailabilityType.UNKNOWN },
                onValueSelected = { availabilityType = it }
            )

            // Night care
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Night Care")
                Switch(
                    checked = nightCare,
                    onCheckedChange = { nightCare = it }
                )
            }

            // Skill (single-select for now)
            ItemDropdownField(
                label = "Skill",
                items = skills,
                selectedItem = selectedSkill,
                onItemSelected = { selectedSkill = it },
                itemLabel = { it.name }
            )

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving,
                onClick = {
                    caretakerProfileViewModel.updateCaretakerProfile(
                        bio = bio,
                        caretakerType = caretakerType,
                        availabilityType = availabilityType,
                        nightCare = nightCare,
                        skillIds = selectedSkill?.let { listOf(it.id) } ?: emptyList()
                    ) { success, _ ->
                        if (success) {
                            navController.popBackStack()
                        }
                    }
                }
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save Changes")
                }
            }
        }
    }
}
