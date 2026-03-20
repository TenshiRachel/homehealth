package com.example.homehealth.location

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters

class SyncWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): ListenableWorker.Result {
        // Collect location and save to Firebase
        LocationCollector.collect(applicationContext)
        return ListenableWorker.Result.success()
    }
}
