package com.ada.mybuffet.screens.news

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ada.mybuffet.repo.FinnhubApi
import com.ada.mybuffet.repo.StockShare
import com.ada.mybuffet.repo.SymbolPressResponse
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.IOException
import com.ada.mybuffet.screens.myShares.repo.MySharesRepository.Companion.shareItemStringList
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author Selin Bilge
 */
class NewsModel() {
    val firestore = Firebase.firestore

    suspend fun loadAll(): ArrayList<SymbolPressResponse>{
        // list shows the newest share item at first
        shareItemStringList.reverse()

        var list = ArrayList<SymbolPressResponse>()
        var time = System.currentTimeMillis()
        val scopeList = mutableListOf<Deferred<Boolean?>>()

        for (i in 0..shareItemStringList.size-1 ){
            var test = CoroutineScope(Dispatchers.IO).async {
                Log.d("LOG",(System.currentTimeMillis()-time).toString())
                var test = getNews(shareItemStringList.get(i))
                test?.let {
                    list.add(test)
                }
            }
            scopeList.add(test)
        }
        scopeList.awaitAll()
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