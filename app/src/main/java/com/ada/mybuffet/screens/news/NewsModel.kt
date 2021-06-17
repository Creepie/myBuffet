package com.ada.mybuffet.screens.news

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ada.mybuffet.repo.FinnhubApi
import com.ada.mybuffet.repo.SymbolPressResponse
import kotlinx.coroutines.*
import java.io.IOException

class NewsModel() {

    var urlList = arrayListOf<String>(
        "https://finnhub.io/api/v1/press-releases?symbol=AAPL&token=sandbox_c2vgcniad3i9mrpv9cn0",
        "https://finnhub.io/api/v1/press-releases?symbol=VOE.VI&token=sandbox_c2vgcniad3i9mrpv9cn0"
    )

    suspend fun loadAll(): ArrayList<SymbolPressResponse>{
        var list = ArrayList<SymbolPressResponse>()
        var time = System.currentTimeMillis()
        val scopeList = mutableListOf<Deferred<Boolean?>>()
        for (url in urlList){
            var test = CoroutineScope(Dispatchers.IO).async {
                Log.d("LOG",(System.currentTimeMillis()-time).toString())
                var test = getNews(url)
                test?.let {
                    list.add(test)
                }
            }
            scopeList.add(test)
        }
        scopeList.awaitAll()
        //todo sort list

        return list
    }

    suspend fun getNews(url: String) :SymbolPressResponse?{
            return try {
                FinnhubApi.retrofitService.getPressNews(url)
            } catch (networkError: IOException){
                Log.i("API","fetchNews Error with code: ${networkError.message}")
                null;
            }
    }
}