package com.example.homehealth.ui.textfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Enum<T>> EnumDropdownField(
    label: String,
    selectedValue: T,
    values: Array<T>,
    onValueSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    exclude: (T) -> Boolean = { false }
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedValue
                .takeIf { !exclude(it) }
                ?.name
                ?.replace("_", " ")
                ?.lowercase()
                ?.replaceFirstChar { it.uppercase() }
                ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            values
                .filterNot(exclude)
                .forEach { value ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                value.name
                                    .replace("_", " ")
                                    .lowercase()
                                    .replaceFirstChar { it.uppercase() }
                            )
                        },
                        onClick = {
                            onValueSelected(value)
                            expanded = false
                        }
                    )
                }
        }
    }
}