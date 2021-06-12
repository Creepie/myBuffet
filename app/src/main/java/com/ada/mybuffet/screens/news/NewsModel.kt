package com.ada.mybuffet.screens.news

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ada.mybuffet.repo.FinnhubApi
import com.ada.mybuffet.repo.SymbolPressResponse
import kotlinx.coroutines.*
import java.io.IOException

class NewsModel(var viewModelScope: CoroutineScope) {

    var list = ArrayList<SymbolPressResponse>()

    var urlList = ArrayList<String>()

    private var _news = MutableLiveData<MutableList<SymbolPressResponse>>()

    val news: LiveData<MutableList<SymbolPressResponse>>
        get() = _news


    fun loadAll(){
        urlList.add("https://finnhub.io/api/v1/press-releases?symbol=AAPL&token=sandbox_c2vgcniad3i9mrpv9cn0")
        urlList.add("https://finnhub.io/api/v1/press-releases?symbol=VOE.VI&token=sandbox_c2vgcniad3i9mrpv9cn0")
        for (url in urlList){
            getNews(url)
        }
    }

    fun getNews(url: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val test = FinnhubApi.retrofitService.getPressNews(url)
                list.add(test)
                _news.postValue(list)
            } catch (networkError: IOException){
                Log.i("API","fetchNews Error with code: ${networkError.message}")
            }
        }
    }
}