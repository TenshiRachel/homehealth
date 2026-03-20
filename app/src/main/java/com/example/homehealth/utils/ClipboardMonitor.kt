package com.example.homehealth.utils

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

object ClipboardMonitor {
    private val _clipboardText = mutableStateOf<String?>(null)
    val clipboardText: State<String?> = _clipboardText
    fun updateText(text: String?) {
        _clipboardText.value = text
    }
}
