package com.ada.mybuffet.screens.stocks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ada.mybuffet.repo.StockShare
import kotlinx.coroutines.*

class StocksOverviewViewModel(application: Application): AndroidViewModel(application) {
    var model = StocksOverviewModel()

    private var _stocks = MutableLiveData<MutableList<StockShare>>()

    val stocks: LiveData<MutableList<StockShare>>
    get() = _stocks

    init {
        loadStockData()
    }

    private fun loadStockData(){
        CoroutineScope(Dispatchers.IO).launch {
            _stocks.postValue(model.loadSharesFromFirebase(model.indexList[0]))
        }
    }

    fun chanceStockData(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            _stocks.postValue(model.loadSharesFromFirebase(model.indexList[position]))
        }
    }

    suspend fun loadAllStocks(){
        val scopeList = ArrayList<Deferred<Boolean>>()
        var list = ArrayList<StockShare>()
        for (stock in model.indexList){
            val scope = CoroutineScope(Dispatchers.IO).async {
                list.addAll(model.loadSharesFromFirebase(stock))
            }
            scopeList.add(scope)
        }
        scopeList.awaitAll()
        list = list.distinctBy { it.symbol } as ArrayList<StockShare>
        _stocks.postValue(list)
    }
}