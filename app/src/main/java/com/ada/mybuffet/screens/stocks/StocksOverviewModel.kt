package com.ada.mybuffet.screens.stocks

import android.util.Log
import com.ada.mybuffet.repo.*
import com.google.firebase.inject.Deferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import retrofit2.http.Url
import java.io.IOException

class StocksOverviewModel {

    var indexList = arrayListOf<String>(
        "https://finnhub.io/api/v1/index/constituents?symbol=^GDAXI&token=c2vgcniad3i9mrpv9cmg"
    )

    var stockShares = ArrayList<StockShare>()
    var stockList = ArrayList<IndexSymbols>()


    suspend fun loadStockList(): ArrayList<IndexSymbols>{
        stockList = loadIndexList()
        return stockList
    }

    suspend fun load(): ArrayList<StockShare>{
        var shares = ArrayList<StockShare>()
        var dividends = loadDividends(stockList)
        var currentPrices = 1
        return shares
    }

   suspend fun loadIndexList(): ArrayList<IndexSymbols>{
       var list = ArrayList<IndexSymbols>()
       val scopeList = mutableListOf<kotlinx.coroutines.Deferred<Boolean?>>()
       for(index in indexList){
           var scope = CoroutineScope(Dispatchers.IO).async {
               var data = loadIndex(index)
               data?.let {
                   list.add(data)
               }
           }
           scopeList.add(scope)
       }
       scopeList.awaitAll()
       return list
   }

    suspend fun loadIndex(url: String): IndexSymbols?{
        return try {
            FinnhubApi.retrofitService.getIndexSymbols(url)
        } catch (networkError: IOException){
            Log.i("API", "fetchIndex Error with code: ${networkError.message}")
            null;
        }
    }

    suspend fun loadDividends(indexList: ArrayList<IndexSymbols>): ArrayList<Dividends>{
        var list = ArrayList<Dividends>()
        val scopeList = mutableListOf<kotlinx.coroutines.Deferred<Boolean?>>()
        for (index in indexList){
            for(company in index.constituents){
                var scope = CoroutineScope(Dispatchers.IO).async {
                    var data = loadDividend(company)
                    data?.let {
                        list.add(data)
                    }
                }
                scopeList.add(scope)
            }
        }
        scopeList.awaitAll()
        return list
    }

    suspend fun loadDividend(symbol: String): Dividends?{
        return try {
            val url = "https://finnhub.io/api/v1/stock/dividend2?symbol=${symbol}&token=sandbox_c2vgcniad3i9mrpv9cn0"
            FinnhubApi.retrofitService.getDividends(url)
        } catch (networkError: IOException){
            Log.i("API", "fetchIndex Error with code: ${networkError.message}")
            null
        }
    }




}