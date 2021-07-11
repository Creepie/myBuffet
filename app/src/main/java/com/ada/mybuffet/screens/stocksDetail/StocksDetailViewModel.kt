package com.ada.mybuffet.screens.stocksDetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ada.mybuffet.repo.StockCandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Mario Eberth
 */
class StocksDetailViewModel(application: Application): AndroidViewModel(application) {
    var model = StocksDetailModel()

    private val _candles: MutableLiveData<StockCandle> by lazy {
        MutableLiveData<StockCandle>()
    }

    val candles: LiveData<StockCandle>
        get() = _candles


    fun loadCandleData(symbol: String){
        viewModelScope.launch {
            //hardcoded from / to > stop working here because the api dont crawl the right data with the sandbox key
            var test = model.loadCandle(symbol,"1615298999","1615302599")
            _candles.postValue(test)
        }
    }

}