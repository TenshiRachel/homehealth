package com.example.homehealth.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> FilterChipsRow(
    title: String,
    options: List<T>,
    selected: T?,
    onSelectedChange: (T?) -> Unit,
    label: (T) -> String
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge
        )

        LazyColumn(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.wrapContentHeight()
        ) {
            item {
                androidx.compose.foundation.layout.Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // "All" / no filter
                    AssistChip(
                        onClick = { onSelectedChange(null) },
                        label = { Text("All") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selected == null)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surface,
                            labelColor = if (selected == null)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    )

                    options.forEach { option ->
                        AssistChip(
                            onClick = { onSelectedChange(option) },
                            label = { Text(label(option)) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selected == option)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surface,
                                labelColor = if (selected == option)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    }
}