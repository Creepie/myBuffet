package com.ada.mybuffet.screens.stocks

import android.util.Log
import com.ada.mybuffet.repo.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.IOException

class StocksOverviewModel {

    //this is the list of all supported stocks
    var indexList = arrayListOf<String>(
        "^GDAXI", "^DJI", "^NDX", "^STOXX50E"
    )

    val firestore = Firebase.firestore

    /**
     * this method creates StockShares and fill it with information from different api calls
     * @see StockShare
     * @param startIndexes is the index where to start in the ArrayList of IndexSymbols
     * @param endIndex is the index where to stop in the ArrayList of IndexSymbols
     * @see IndexSymbols
     * this is to solve the problem with the 60 api calls / min
     * @param symbolList is a list of symbols from each supported index (param for api calls)
     * @return a ArrayList of StockShare which contains shares with info's like name, currentPrice, dividends,...
     */
    suspend fun loadShares(
        startIndexes: StockIndexes,
        endIndex: StockIndexes,
        symbolList: ArrayList<IndexSymbols>
    ): ArrayList<StockShare>{

        var stockShares = ArrayList<StockShare>()
        val list = mutableListOf<Deferred<Boolean>>()
        var time = System.currentTimeMillis()
        for (i in startIndexes.shareIndex..endIndex.shareIndex){
            var symbol = symbolList[startIndexes.stockIndex].constituents[i]

            var scope = CoroutineScope(Dispatchers.IO).async {
                val scopeList = mutableListOf<Deferred<Int>>()
                var share = StockShare(symbol = symbol)
                //go into Scope for the divis
                var scopeDivis = CoroutineScope(Dispatchers.IO).async {
                    var dividends = loadDividend(symbol)
                    share.dividends = dividends
                    Log.d("LOG","$i divi rdy ${(System.currentTimeMillis()-time)}")
                }
                scopeList.add(scopeDivis)
                //go into Scope for the current price
                var scopeCurPrice = CoroutineScope(Dispatchers.IO).async {
                    var price = loadCurrentPrice(symbol)
                    share.curPrice = price?.c
                    Log.d("LOG","$i curPrice rdy ${(System.currentTimeMillis()-time)}")
                }
                scopeList.add(scopeCurPrice)
                //go into Scope for the name
                var scopeName = CoroutineScope(Dispatchers.IO).async {
                    var name = loadShareName(symbol)
                    name.let {
                        if (name?.result?.isNotEmpty() == true){
                            share.name = name?.result?.get(0)?.description
                        }
                    }
                    Log.d("LOG","$i name rdy ${(System.currentTimeMillis()-time)}")
                }
                Log.d("LOG","$i all scopes started ${(System.currentTimeMillis()-time)}")
                scopeList.add(scopeName)
                scopeList.awaitAll()
                Log.d("LOG","$i all scopes done ${(System.currentTimeMillis()-time)}")
                stockShares.add(share)
            }
            list.add(scope)
        }
        list.awaitAll()
        return stockShares
    }

    /**
     * this method loads the IndexList of all supported index
     * @return a list with stocks which contains a list with symbols in this stock
     */
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

    /**
     * this method load one stock with the symbols in it
     * @param index contains the index name like ^GDAXI for DAX (German stock)
     * @return a IndexSymbols obj where all symbols of the DAX are in
     * @see IndexSymbols
     */
    private suspend fun loadIndex(index: String): IndexSymbols?{
        return try {
            var url = "https://finnhub.io/api/v1/index/constituents?symbol=${index}&token=c2vgcniad3i9mrpv9cmg"
            FinnhubApi.retrofitService.getIndexSymbols(url)
        } catch (networkError: IOException){
            Log.i("API", "fetchIndex Error with code: ${networkError.message}")
            null;
        }
    }

    /**
     * this method load the last dividends of a share
     * @param symbol is the current symbol of the share
     * @return a Dividends obj where all dividends of this share are in
     * @see Dividends
     */
    private suspend fun loadDividend(symbol: String): Dividends?{
        return try {
            val url = "https://finnhub.io/api/v1/stock/dividend2?symbol=${symbol}&token=sandbox_c2vgcniad3i9mrpv9cn0"
            FinnhubApi.retrofitService.getDividends(url)
        } catch (networkError: IOException){
            Log.i("API", "fetchIndex Error with code: ${networkError.message}")
            null
        }
    }

    /**
     * this method load the current price of a share
     * @param symbol is the current symbol of the share
     * @return a SymbolPrice obj where the prices are in (not only the current price, but we only need theme)
     * @see SymbolPrice
     */
    private suspend fun loadCurrentPrice(symbol: String): SymbolPrice?{
        return try {
            val url = "https://finnhub.io/api/v1/quote?symbol=${symbol}&token=sandbox_c2vgcniad3i9mrpv9cn0"
            FinnhubApi.retrofitService.getCurrentPrice(url)
        } catch (networkError: IOException){
            Log.i("API", "fetchIndex Error with code: ${networkError.message}")
            null
        }
    }

    /**
     * this method load the name of the share
     * @param symbol is the current symbol of the share
     * @return a SymbolLookupResponse obj which contains a list of results (only take the result[0])
     * @see SymbolLookupResponse
     */
    private suspend fun loadShareName(symbol: String): SymbolLookupResponse?{
        return try {
            val url = "https://finnhub.io/api/v1/search?q=${symbol}&token=sandbox_c2vgcniad3i9mrpv9cn0"
            FinnhubApi.retrofitService.getName(url)
        } catch (networkError: IOException){
            Log.i("API", "fetchIndex Error with code: ${networkError.message}")
            null
        }
    }

    /**
     * this method loads Shares from firebase db from a specific stock
     * @param stockSymbol is the stock symbol we are looking for
     * @return a list of StockShare and the list represent one Stock like the DAX
     */
    suspend fun loadSharesFromFirebase(stockSymbol: String): ArrayList<StockShare>{
        var list = ArrayList<StockShare>()
            val docRef = firestore.collection(stockSymbol)
            var stocksList = docRef.get()
                .addOnSuccessListener { documents ->
                    documents.forEach { document ->
                        if (document != null){
                            try {
                                list.add(document.toObject(StockShare::class.java))
                            } catch (e: Exception){

                            }
                        }
                    }
                }
            stocksList.await()
        return list
    }
}