package com.ada.mybuffet.screens.stocks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ada.mybuffet.repo.StockShare
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StocksOverviewViewModel(application: Application): AndroidViewModel(application) {
    var model = StocksOverviewModel()

    private var _stocks = MutableLiveData<MutableList<StockShare>>()

    val stocks: LiveData<MutableList<StockShare>>
    get() = _stocks

    init {
        loadStockData()
    }

    fun loadStockData(){
        CoroutineScope(Dispatchers.IO).launch {
            _stocks.postValue(model.loadSharesFromFirebase(model.indexList[0]))
        }
    }

    fun chanceStockData(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            _stocks.postValue(model.loadSharesFromFirebase(model.indexList[position]))
        }
    }


}