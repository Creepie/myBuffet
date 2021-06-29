package com.ada.mybuffet.screens.stocks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ada.mybuffet.repo.StockShare
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
       //todo Firebase API call
    }

    fun reloadStockData(){
        //todo Firebase API call
    }
}