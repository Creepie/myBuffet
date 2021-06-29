package com.ada.mybuffet

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.work.*
import com.ada.mybuffet.repo.worker.RefreshStockSymbolWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class myBuffetApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            Log.i("WORKER_RUNNING","delayedInit start")
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork(){
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .apply {
                setRequiresDeviceIdle(false)
            }
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshStockSymbolWorker>(16, TimeUnit.MINUTES)
            .setConstraints(constraints).build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshStockSymbolWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }
}