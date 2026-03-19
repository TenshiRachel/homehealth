package com.example.homehealth.accessibility

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.text.InputType
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.homehealth.utils.AccessibilityTelemetryLogger
import java.util.Locale

class HomeHealthAccessibilityService : AccessibilityService(), TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    private var lastAnnouncement = ""
    private var lastSpokenWord = ""
    private val speechHandler = Handler(Looper.getMainLooper())
    private var pendingTypedTextRunnable: Runnable? = null
    private var lastPasswordSnapshot = ""
    private var currentForegroundPackage: String = ""

    override fun onServiceConnected() {
        super.onServiceConnected()
        textToSpeech = TextToSpeech(this, this)
        speak("Accessibility enabled.")
        logTelemetry("service_connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        logAccessibilityEventDetails(event)

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                currentForegroundPackage = event.packageName?.toString().orEmpty()
                announceWindowChange(event)
            }
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> announceKeyboardTypeIfNeeded(event)
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> announceTypedTextIfNeeded(event)
        }
    }

    private fun logAccessibilityEventDetails(event: AccessibilityEvent) {
        val source = event.source
        val sourceClassName = source?.className?.toString().orEmpty()
        val sourceViewId = source?.viewIdResourceName.orEmpty()
        val sourceInputType = source?.inputType ?: 0
        val isPasswordField = event.isPassword || isPasswordInputType(sourceInputType)

        val eventText = event.text
            ?.joinToString(" ")
            ?.trim()
            .orEmpty()

        val payload = mutableMapOf<String, Any?>(
            "eventTypeInt" to event.eventType,
            "eventTypeName" to AccessibilityEvent.eventTypeToString(event.eventType),
            "packageName" to event.packageName?.toString().orEmpty(),
            "className" to event.className?.toString().orEmpty(),
            "sourceClassName" to sourceClassName,
            "sourceViewId" to sourceViewId,
            "sourceInputType" to sourceInputType,
            "isPasswordEvent" to event.isPassword,
            "isPasswordField" to isPasswordField,
            "isEnabled" to event.isEnabled,
            "isChecked" to event.isChecked,
            "isFullScreen" to event.isFullScreen,
            "isScrollable" to event.isScrollable,
            "contentDescription" to event.contentDescription,
            "eventText" to eventText,
            "beforeText" to event.beforeText,
            "fromIndex" to event.fromIndex,
            "toIndex" to event.toIndex,
            "itemCount" to event.itemCount,
            "currentItemIndex" to event.currentItemIndex,
            "addedCount" to event.addedCount,
            "removedCount" to event.removedCount,
            "recordCount" to event.recordCount,
            "action" to event.action,
            "contentChangeTypes" to event.contentChangeTypes,
            "movementGranularity" to event.movementGranularity,
            "eventTime" to event.eventTime
        )

        logTelemetry("accessibility_event", payload)
    }

    override fun onInterrupt() {
        textToSpeech?.stop()
        Log.d(TAG, "Accessibility service interrupted")
        logTelemetry("service_interrupted")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.getDefault()
        }
    }

    override fun onDestroy() {
        pendingTypedTextRunnable?.let { speechHandler.removeCallbacks(it) }
        pendingTypedTextRunnable = null

        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        logTelemetry("service_destroyed")
        super.onDestroy()
    }

    private fun announceWindowChange(event: AccessibilityEvent) {
        val windowText = event.text?.joinToString(separator = " ")?.trim().orEmpty()
        val className = event.className?.toString().orEmpty()
        val spoken = when {
            windowText.isNotBlank() -> "Screen changed. $windowText"
            className.isNotBlank() -> "Screen changed. ${className.substringAfterLast('.')}"
            else -> "Screen changed."
        }

        speakDeduplicated(spoken)
        Log.d(TAG, "Window change: $spoken")
        if (className.isNotBlank()) {
            logTelemetry("screen_changed", mapOf("screenClass" to className))
        }
    }

    private fun announceKeyboardTypeIfNeeded(event: AccessibilityEvent) {
        val source = event.source ?: return
        val className = source.className?.toString().orEmpty()
        if (!className.contains("EditText", ignoreCase = true)) {
            return
        }

        val keyboardType = describeInputType(source.inputType)
        speak("Keyboard type: $keyboardType")
        Log.d(TAG, "Keyboard type detected: $keyboardType")
        logTelemetry("keyboard_type_detected", mapOf("keyboardType" to keyboardType))
    }

    private fun describeInputType(inputType: Int): String {
        val baseClass = inputType and InputType.TYPE_MASK_CLASS

        if ((inputType and InputType.TYPE_TEXT_VARIATION_PASSWORD) != 0 ||
            (inputType and InputType.TYPE_NUMBER_VARIATION_PASSWORD) != 0
        ) {
            return "password"
        }

        if ((inputType and InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) != 0) {
            return "email"
        }

        if ((inputType and InputType.TYPE_TEXT_VARIATION_URI) != 0) {
            return "URL"
        }

        return when (baseClass) {
            InputType.TYPE_CLASS_NUMBER -> "numeric"
            InputType.TYPE_CLASS_PHONE -> "phone"
            InputType.TYPE_CLASS_DATETIME -> "date and time"
            else -> "text"
        }
    }

    private fun announceTypedTextIfNeeded(event: AccessibilityEvent) {
        val source = event.source ?: return
        val className = source.className?.toString().orEmpty()
        if (!source.isEditable && !className.contains("EditText", ignoreCase = true)) {
            return
        }

        val isPasswordField = isPasswordInputType(source.inputType) || event.isPassword
        if (isPasswordField) {
            announcePasswordText(source.text?.toString().orEmpty().trim())
            return
        }

        val fullText = source.text?.toString().orEmpty().trim()
        if (fullText.isBlank()) {
            return
        }

        pendingTypedTextRunnable?.let { speechHandler.removeCallbacks(it) }
        pendingTypedTextRunnable = Runnable {
            val wordToSpeak = fullText
                .split(Regex("\\s+"))
                .lastOrNull()
                ?.trim('.', ',', '!', '?', ';', ':')
                .orEmpty()

            if (wordToSpeak.isNotBlank() && wordToSpeak != lastSpokenWord) {
                lastSpokenWord = wordToSpeak
                speak(wordToSpeak)
                Log.d(TAG, "Typed word echoed: $wordToSpeak")
            }
        }
        speechHandler.postDelayed(pendingTypedTextRunnable!!, TYPING_ECHO_DEBOUNCE_MS)
    }

    private fun announcePasswordText(fullText: String) {
        if (fullText.isEmpty()) {
            if (lastPasswordSnapshot.isNotEmpty()) {
                speak("password cleared")
            }
            lastPasswordSnapshot = fullText
            return
        }

        when {
            fullText.length > lastPasswordSnapshot.length -> {
                val addedCount = fullText.length - lastPasswordSnapshot.length
                val addedSegment = fullText.takeLast(addedCount)

                if (containsMaskCharactersOnly(addedSegment)) {
                    speak("character entered")
                } else {
                    speak(addedSegment.toCharArray().joinToString(" "))
                }
            }

            fullText.length < lastPasswordSnapshot.length -> {
                speak("character deleted")
            }

            fullText != lastPasswordSnapshot -> {
                if (containsMaskCharactersOnly(fullText)) {
                    speak("password updated")
                } else {
                    speak(fullText.toCharArray().joinToString(" "))
                }
            }
        }

        lastPasswordSnapshot = fullText
    }

    private fun containsMaskCharactersOnly(value: String): Boolean {
        if (value.isBlank()) return false
        return value.all { it == BULLET_CHAR || it == STAR_CHAR || it == BLACK_CIRCLE_CHAR }
    }

    private fun safeText(value: String, redact: Boolean): String {
        if (value.isBlank()) return ""
        if (redact) return "[REDACTED]"

        return if (value.length > MAX_TELEMETRY_TEXT_LENGTH) {
            value.take(MAX_TELEMETRY_TEXT_LENGTH)
        } else {
            value
        }
    }

    private fun isPasswordInputType(inputType: Int): Boolean {
        return (inputType and InputType.TYPE_TEXT_VARIATION_PASSWORD) != 0 ||
            (inputType and InputType.TYPE_NUMBER_VARIATION_PASSWORD) != 0 ||
            (inputType and InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) != 0 ||
            (inputType and InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD) != 0
    }

    private fun speakDeduplicated(message: String) {
        if (message == lastAnnouncement) {
            return
        }
        lastAnnouncement = message
        speak(message)
    }

    private fun speak(message: String) {
        if (currentForegroundPackage.isNotEmpty() && currentForegroundPackage != packageName) return
        textToSpeech?.speak(message, TextToSpeech.QUEUE_FLUSH, null, message.hashCode().toString())
    }

    companion object {
        private const val TAG = "HHAccessibilityService"
        private const val TYPING_ECHO_DEBOUNCE_MS = 450L
        private const val MAX_TELEMETRY_TEXT_LENGTH = 300
        private const val BULLET_CHAR = '•'
        private const val STAR_CHAR = '*'
        private const val BLACK_CIRCLE_CHAR = '●'
    }

    private fun logTelemetry(eventType: String, attributes: Map<String, Any?> = emptyMap()) {
        AccessibilityTelemetryLogger.logEvent(this, eventType, attributes)
    }
}
