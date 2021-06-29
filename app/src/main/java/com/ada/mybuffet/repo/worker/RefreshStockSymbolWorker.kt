package com.ada.mybuffet.repo.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ada.mybuffet.repo.IndexSymbols
import com.ada.mybuffet.screens.stocks.StocksOverviewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import retrofit2.HttpException

class RefreshStockSymbolWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "com.ada.mybuffet.repo.worker.RefreshStockSymbolWorker.kt"
    }

    override suspend fun doWork(): Result {
        Log.i("WORKER_RUNNING", "Worker Refresh Stock Symbol is running")
        val model = StocksOverviewModel()
        val firestore = Firebase.firestore
        try {
           val data = model.loadIndexList()
            for (stock in data){
                val store = hashMapOf(
                    "StockSymbol" to stock.symbol,
                    "symbols" to stock.constituents
                )
                firestore.collection("stocks").document(stock.symbol).set(store)
            }
            Log.i("WORKER_RUNNING", "Refresh Stock got the data")
        } catch (e: HttpException){
            Log.i("WORKER_RUNNING", "Refresh Stock on failure")
            return Result.retry()
        }
        return Result.success()
    }
}