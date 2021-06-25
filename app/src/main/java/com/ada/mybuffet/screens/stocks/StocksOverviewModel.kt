package com.ada.mybuffet.screens.stocks

import android.util.Log
import com.ada.mybuffet.repo.*
import kotlinx.coroutines.*
import java.io.IOException

class StocksOverviewModel {

    var indexList = arrayListOf<String>(
        "https://finnhub.io/api/v1/index/constituents?symbol=^GDAXI&token=c2vgcniad3i9mrpv9cmg"
    )

    var stockShares = ArrayList<StockShare>()
    var stockList = ArrayList<IndexSymbols>()


    suspend fun loadStockList(): Int{
        stockList = loadIndexList()
        var count = 0
        if (stockList.isNotEmpty()){
            count = stockList[0].constituents.size
        }
        return count
    }

    suspend fun load(): ArrayList<StockShare>{
        val list = mutableListOf<Deferred<Boolean>>()
        //todo for loop over 5 shares
        for (i in 1..5){
            var symbol = stockList[0].constituents[i]
            var scope = CoroutineScope(Dispatchers.IO).async {
                val scopeList = mutableListOf<Deferred<Unit>>()
                var share = StockShare(symbol = symbol)
                //go into Scope for the divis
                var scopeDivis = CoroutineScope(Dispatchers.IO).async {
                    var dividends = loadDividend(symbol)
                    share.dividends = dividends
                }
                scopeList.add(scopeDivis)
                //go into Scope for the current price
                var scopeCurPrice = CoroutineScope(Dispatchers.IO).async {
                    var price = loadCurrentPrice(symbol)
                    share.curPrice = price?.c
                }
                scopeList.add(scopeCurPrice)
                //go into Scope for the name
                var scopeName = CoroutineScope(Dispatchers.IO).async {
                    var name = loadShareName(symbol)
                    share.name = name?.result?.get(0)?.description
                }
                scopeList.add(scopeName)
                scopeList.awaitAll()
                stockShares.add(share)
            }
            list.add(scope)
        }
        list.awaitAll()
        return stockShares
    }

   suspend fun loadIndexList(): ArrayList<IndexSymbols>{
       var list = ArrayList<IndexSymbols>()
       val scopeList = mutableListOf<Deferred<Boolean?>>()
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

    suspend fun loadCurrentPrice(symbol: String): SymbolPrice?{
        return try {
            val url = "https://finnhub.io/api/v1/quote?symbol=${symbol}&token=sandbox_c2vgcniad3i9mrpv9cn0"
            FinnhubApi.retrofitService.getCurrentPrice(url)
        } catch (networkError: IOException){
            Log.i("API", "fetchIndex Error with code: ${networkError.message}")
            null
        }
    }

    suspend fun loadShareName(symbol: String): SymbolLookupResponse?{
        return try {
            val url = "https://finnhub.io/api/v1/search?q=${symbol}&token=sandbox_c2vgcniad3i9mrpv9cn0"
            FinnhubApi.retrofitService.getName(url)
        } catch (networkError: IOException){
            Log.i("API", "fetchIndex Error with code: ${networkError.message}")
            null
        }
    }




}