package com.example.homehealth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Silently re‑schedule the "sync" job after reboot
            WorkScheduler.schedule(context)
        }
    }
}
