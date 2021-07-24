package com.ada.mybuffet.screens.news

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ada.mybuffet.repo.FinnhubApi
import com.ada.mybuffet.repo.StockShare
import com.ada.mybuffet.repo.SymbolPressResponse
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.IOException

class NewsModel() {

    val map = mapOf("DAX" to "^GDAXI", "Dow Jones" to "^DJI", "Nasdaq 100" to "^NDX", "Euro Stoxx 50" to "^STOXX50E" )

    //this is the list of all supported stocks
    var indexList = map.values.toList()
    val firestore = Firebase.firestore


    var urlList = arrayListOf<String>(
        "https://finnhub.io/api/v1/press-releases?symbol=AAPL&token=sandbox_c2vgcniad3i9mrpv9cn0",
        "https://finnhub.io/api/v1/press-releases?symbol=VOE.VI&token=sandbox_c2vgcniad3i9mrpv9cn0"
    )


    /*
    var urlList = arrayListOf<String>(
        "AAPL",
        "VOE.VI"
    )

     */

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


    /**
     * this method loads Shares from firebase db from a specific stock
     * @param stockSymbol is the stock symbol we are looking for
     * @return a list of StockShare and the list represent one Stock like the DAX
     */
    suspend fun loadSharesFromFirebase(stockSymbol: String): ArrayList<SymbolPressResponse>{
        val list = ArrayList<SymbolPressResponse>()
        val docRef = firestore.collection(stockSymbol)
        val stocksList = docRef.get()
            .addOnSuccessListener { documents ->
                documents.forEach { document ->
                    if (document != null){
                        try {
                            list.add(document.toObject(SymbolPressResponse::class.java))
                        } catch (e: Exception){

                        }
                    }
                }
            }
        stocksList.await()
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