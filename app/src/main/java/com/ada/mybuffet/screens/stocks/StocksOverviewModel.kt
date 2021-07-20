package com.ada.mybuffet.screens.stocks

import android.util.Log
import com.ada.mybuffet.repo.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.IOException

/**
 * @author Mario Eberth
 * this class is the Model of the Stocks Overview
 * at least the most methods are used with the workManager the UI only need the loadSharesFromFirebase method
 * (because of the api limit i have to schedule the api calls to get the job done)
 */
class StocksOverviewModel {

    val map = mapOf("DAX" to "^GDAXI", "Dow Jones" to "^DJI", "Nasdaq 100" to "^NDX", "Euro Stoxx 50" to "^STOXX50E" )

    //this is the list of all supported stocks
    var indexList = map.values.toList()

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
    ): ArrayList<StockShare> = withContext(Dispatchers.IO){
        val stockShares = ArrayList<StockShare>()
        val scopeList = mutableListOf<Deferred<Boolean>>()
        val time = System.currentTimeMillis()

        for (i in startIndexes.shareIndex..endIndex.shareIndex){

            val symbol = symbolList[startIndexes.stockIndex].constituents[i]
            val scope = async {
                stockShares.add(getShare(symbol,time))
            }
            scopeList.add(scope)
        }
        scopeList.awaitAll()
        return@withContext stockShares
    }


    /**
     * this method creates a stockShare, fetches data from api and add theme to the stockShare
     * @param symbol is the symbol of the share
     * @param time is to log when the function is done
     * @return the filled stockShare
     * @see StockShare
     */
    private suspend fun getShare(symbol: String, time: Long): StockShare = withContext(Dispatchers.IO){

        val scopeList = mutableListOf<Deferred<Unit>>()
        val share = StockShare(symbol = symbol)
        //go into Scope for the divis
        val scopeDividends = async {
            share.dividends = getDividend(symbol,time)
        }
        scopeList.add(scopeDividends)
        //go into Scope for the current price
        val scopeCurPrice = async {
            var data = getCurrentPrice(symbol,time)
            share.curPrice = data.first
            share.prevClosePrice = data.second
        }
        scopeList.add(scopeCurPrice)
        //go into Scope for the name
        val scopeName = async {
            share.name = getShareName(symbol,time)
        }
        Log.d("LOG","$symbol all scopes started ${(System.currentTimeMillis()-time)}")
        scopeList.add(scopeName)
        scopeList.awaitAll()
        Log.d("LOG","$symbol all scopes done ${(System.currentTimeMillis()-time)}")
        return@withContext share
    }

    /**
     * this method gets the dividends of a symbol if api gets no result the list is empty
     * @param symbol is the current symbol of the share
     * @param time is to log when the function is done
     * @return the dividends of a share
     */
    private suspend fun getDividend(symbol: String, time: Long): Dividends? = withContext(Dispatchers.IO){
        val dividends = loadDividend(symbol)
        Log.d("LOG","$symbol divi rdy ${(System.currentTimeMillis()-time)}")
        return@withContext dividends
    }

    /**
     * this method gets the current Price of a share if api gets no result the price is 0.0
     * @param symbol is the current symbol of the share
     * @param time is to log when the function is done
     * @return the current price of the share
     */
    private suspend fun getCurrentPrice(symbol: String, time: Long): Pair<Double,Double> = withContext(Dispatchers.IO){
        var curPrice = Pair(0.0,0.0)
        val price = loadCurrentPrice(symbol)
            if (price != null) {
                curPrice = Pair(price.c,price.pc)
            }
        Log.d("LOG","$symbol curPrice rdy ${(System.currentTimeMillis()-time)}")
        return@withContext curPrice
    }

    /**
     * this method gets a shareName of a given symbol
     * if api gets no result the string is empty
     * @param symbol is the current symbol of the share
     * @param time is to log when the function is done
     * @return the name of the share
     */
    private suspend fun getShareName(symbol: String,time: Long): String = withContext(Dispatchers.IO){
        var shareName = ""
        val name = loadShareName(symbol)
            if (name?.result?.isNotEmpty() == true){
                shareName = name.result[0].description
            }
        Log.d("LOG","$symbol name rdy ${(System.currentTimeMillis()-time)}")
        return@withContext shareName
    }

    /**
     * this method loads the IndexList of all supported index
     * @return a list with stocks which contains a list with symbols in this stock
     */
   suspend fun loadIndexList(): ArrayList<IndexSymbols>{
       val list = ArrayList<IndexSymbols>()
       val scopeList = mutableListOf<Deferred<Boolean?>>()
       for(index in indexList){
           val scope = CoroutineScope(Dispatchers.IO).async {
               val data = loadIndex(index)
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
            FinnhubApi.retrofitService.getIndexSymbols(index)
        } catch (networkError: IOException){
            Log.i("API", "fetchIndex Error with code: ${networkError.message}")
            null
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
            FinnhubApi.retrofitService.getDividends(symbol)
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
        return try {FinnhubApi.retrofitService.getCurrentPrice(symbol)
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
            FinnhubApi.retrofitService.getName(symbol)
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
        val list = ArrayList<StockShare>()
            val docRef = firestore.collection(stockSymbol)
            val stocksList = docRef.get()
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