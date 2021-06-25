package com.ada.mybuffet.screens.stocks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class StocksOverviewViewModel(application: Application): AndroidViewModel(application) {
    var model = StocksOverviewModel()

    init {
        loadStockData()
    }

    fun loadStockData(){
        viewModelScope.launch {
            var stockList = model.loadStockList()
            var test = model.load()
            var wa = 1
        }
    }
}