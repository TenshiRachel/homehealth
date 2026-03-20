package com.example.homehealth

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.homehealth.location.SyncWorker
import java.util.concurrent.TimeUnit

object WorkScheduler {
    fun schedule(context: Context) {
        val syncRequest = PeriodicWorkRequest.Builder(SyncWorker::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "AppointmentSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
