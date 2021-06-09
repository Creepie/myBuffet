package com.ada.mybuffet.screens.news

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ada.mybuffet.repo.FinnhubApi
import com.ada.mybuffet.repo.SymbolPressResponse
import kotlinx.coroutines.launch
import java.io.IOException

class NewsViewModel (application: Application) : AndroidViewModel(application){

    private var _news = MutableLiveData<SymbolPressResponse>()

    val news: LiveData<SymbolPressResponse>
        get() = _news

    init {
        getNews()
    }

    fun getNews(){
        viewModelScope.launch {
            try {
                FinnhubApi.retrofitService
                _news.value = FinnhubApi.retrofitService.getPressNews("https://finnhub.io/api/v1/press-releases?symbol=AAPL&token=sandbox_c2vgcniad3i9mrpv9cn0")
                print("x")
            } catch (networkError: IOException){
                print("y")
            }
        }
    }
}