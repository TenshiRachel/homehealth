package com.example.homehealth.ui.textfield

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> MultiSelectDropdownField(
    label: String,
    items: List<T>,
    selectedItems: List<T>,
    onSelectionChange: (List<T>) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val displayText = selectedItems.joinToString(", ") { itemLabel(it) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = displayText,
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
            items.forEach { item ->
                val isSelected = selectedItems.contains(item)

                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null // handled by onClick
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(itemLabel(item))
                        }
                    },
                    onClick = {
                        val newSelection =
                            if (isSelected) {
                                selectedItems - item
                            } else {
                                selectedItems + item
                            }

                        onSelectionChange(newSelection)
                    }
                )
            }
        }
    }
}