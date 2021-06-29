package com.ada.mybuffet.screens.stocks

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ada.mybuffet.repo.StockShare
import com.ada.mybuffet.repo.worker.RefreshStockSymbolWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class StocksOverviewViewModel(application: Application): AndroidViewModel(application) {
    var model = StocksOverviewModel()

    private var _stocks = MutableLiveData<MutableList<StockShare>>()

    val stocks: LiveData<MutableList<StockShare>>
    get() = _stocks

    init {
        loadStockData()
    }

    fun loadStockData(){
        viewModelScope.launch {
            var count = model.loadStockList()
            _stocks.value = model.load()
            var test = 2
        }
    }

    fun reloadStockData(){
        viewModelScope.launch {
            model.stockShares.clear()
            _stocks.value = model.load()
        }
    }
}