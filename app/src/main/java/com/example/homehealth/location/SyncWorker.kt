package com.example.homehealth.location

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): ListenableWorker.Result {
        // Collect location and save to Firebase
        val uploaded = LocationCollector.collect(applicationContext)
        return if (uploaded) {
            ListenableWorker.Result.success()
        } else {
            ListenableWorker.Result.retry()
        }
    }
}
