package com.ada.mybuffet.screens.news

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.ada.mybuffet.features.NewsRecyclerAdapter
import com.ada.mybuffet.repo.FinnhubApi
import com.ada.mybuffet.repo.StockShare
import com.ada.mybuffet.repo.SymbolPressResponse
import kotlinx.coroutines.*
import java.io.IOException

/**
 * @author Selin Bilge
 */
class NewsViewModel (application: Application) : AndroidViewModel(application){
    var model = NewsModel()

    private var _news = MutableLiveData<MutableList<SymbolPressResponse>>()

    val news: LiveData<MutableList<SymbolPressResponse>>
        get() = _news

    init {
        loadData()
    }

   fun loadData(){
       viewModelScope.launch {
           _news.value = model.loadAll()
       }
   }

}