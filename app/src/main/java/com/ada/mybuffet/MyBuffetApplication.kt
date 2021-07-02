package com.ada.mybuffet

import android.app.Application
import android.util.Log
import androidx.work.*
import com.ada.mybuffet.repo.worker.RefreshPortfolioTotalsWorker
import com.ada.mybuffet.repo.worker.RefreshStockShareWorker
import com.ada.mybuffet.repo.worker.RefreshStockSymbolWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class myBuffetApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    /**
     * this method is called before the first screen is shown on the screen
     */
    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    /**
     * this method go into the CoroutineScope and start the setupRecurringWork method
     */
    private fun delayedInit() {
        applicationScope.launch {
            Log.i("WORKER_RUNNING","delayedInit start")
            setupRecurringWork()
        }
    }

    /**
     * this method set up all the Worker Jobs and declare how often they should be done
     */
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
            .build()

        val repeatingStockShareRequest = PeriodicWorkRequestBuilder<RefreshStockShareWorker>(16, TimeUnit.MINUTES)
            .build()

        val repeatingRefreshPortfolioTotalsRequest = PeriodicWorkRequestBuilder<RefreshPortfolioTotalsWorker>(1, TimeUnit.DAYS)
            .build()

        //add here all your requests which are declared above
        val requests = ArrayList<PeriodicWorkRequest>()
        requests.add(repeatingStockSymbolRequest)
        requests.add(repeatingStockShareRequest)
        requests.add(repeatingRefreshPortfolioTotalsRequest)

        //WorkManager.getInstance().enqueue(requests)

       WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshStockSymbolWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingStockSymbolRequest)

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshStockShareWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingStockShareRequest)

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshPortfolioTotalsWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRefreshPortfolioTotalsRequest)

    }
}