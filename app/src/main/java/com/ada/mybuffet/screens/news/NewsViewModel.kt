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
    var model = NewsModel(viewModelScope)



    init {
        model.loadAll()
    }


}