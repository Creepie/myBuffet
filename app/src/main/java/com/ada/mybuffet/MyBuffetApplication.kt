package com.ada.mybuffet

import android.app.Application
import android.util.Log
import androidx.work.*
import com.ada.mybuffet.repo.worker.RefreshStockShareWorker
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

        val repeatingStockSymbolRequest = PeriodicWorkRequestBuilder<RefreshStockSymbolWorker>(2, TimeUnit.HOURS)
            .setConstraints(constraints).build()

        val repeatingStockShareRequest = PeriodicWorkRequestBuilder<RefreshStockShareWorker>(16, TimeUnit.MINUTES)
            .setConstraints(constraints).build()

        val requests = ArrayList<PeriodicWorkRequest>()
        requests.add(repeatingStockSymbolRequest)
        requests.add(repeatingStockShareRequest)

        WorkManager.getInstance().enqueue(requests)

       /* WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshStockSymbolWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshStockShareWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            rep
        )*/
    }
}