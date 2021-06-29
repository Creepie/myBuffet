package com.ada.mybuffet.repo.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ada.mybuffet.repo.IndexSymbols
import com.ada.mybuffet.repo.StockIndexes
import com.ada.mybuffet.repo.StockShare
import com.ada.mybuffet.screens.stocks.StocksOverviewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException

class RefreshStockShareWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    val firestore = Firebase.firestore
    val model = StocksOverviewModel()

    companion object {
        const val WORK_NAME = "com.ada.mybuffet.repo.worker.RefreshStockShareWorker.kt"
    }

    override suspend fun doWork(): Result {
        Log.i("WORKER_RUNNING", "Worker Refresh Stock Symbol is running")

        try {
            model.loadStockList()
            var startIndexes = getStartIndex()
            //get the stock list from firebase
            var list = getStockListFromFirebase()
            //calc the indexes
            var endIndex = calcEndIndex(startIndexes, list)
            //load the shares from the model
            var data = model.loadShares(startIndexes, endIndex, list)
            //save it to firebase
            saveToFirebase(data, list[startIndexes.stockIndex].symbol)
            //
            var newIndex = calcStartIndex(endIndex, list)
            saveStartIndexToFirebase(newIndex)
            var test = 0
        } catch (e: HttpException) {
            return Result.retry()
        }
        return Result.success()
    }

    /**
     * this method is to calc the right end index for the api calls
     * check if out of bounds if yes > set to maximum if not set endIndex
     */
    private fun calcEndIndex(startIndexes: StockIndexes, list: ArrayList<IndexSymbols>): StockIndexes{
        //todo calc end Index for index and shares
        var endIndex = StockIndexes(startIndexes.stockIndex, startIndexes.shareIndex)
        if (startIndexes.shareIndex + 2 >= list[startIndexes.stockIndex].constituents.size){
          endIndex.shareIndex = list[startIndexes.stockIndex].constituents.size-1
        } else {
          endIndex.shareIndex += 2
        }
        return endIndex
    }

    /**
     * this method is to calc the new startIndex for the next api call
     */
    private fun calcStartIndex(startIndexes: StockIndexes, list: ArrayList<IndexSymbols>): StockIndexes{
        //todo calc start Index for index and shares
        if (startIndexes.shareIndex == list[startIndexes.stockIndex].constituents.size-1){
            if (startIndexes.stockIndex == list.size-1){
                startIndexes.stockIndex = 0
            } else {
                startIndexes.stockIndex++
            }
            startIndexes.shareIndex = 0
        }
        return startIndexes
    }

    /**
     * this method is to get the stockList from firebase for the detail api call
     */
    private suspend fun getStockListFromFirebase():ArrayList<IndexSymbols>{
        val docRef = firestore.collection("stocks")
        var list = ArrayList<IndexSymbols>()
        var stocksList = docRef.get()
            .addOnSuccessListener { documents ->
                documents.forEach { document ->
                    if (document != null){
                        list.add(document.toObject(IndexSymbols::class.java))
                    }
                }
            }
        stocksList.await()
        return list
    }


    /**
     * this method saves the data to firebase
     */
    private fun saveToFirebase(data: ArrayList<StockShare>, symbol: String) {
        for (share in data) {
            firestore.collection(symbol).document(share.symbol).set(share)
        }
    }

    /**
     * this method saves the new start index to firebase for the next api call
     */
    private fun saveStartIndexToFirebase(startIndexes: StockIndexes) {
        firestore.collection("stockIndexes").document("index").set(startIndexes)
    }

    /**
     * this method gets the start index from firebase
     */
    private suspend fun getStartIndex(): StockIndexes{
        var indexObj = StockIndexes(0,0)
        val docRef = firestore.collection("stockIndexes").document("index")
        var index = docRef.get().addOnSuccessListener { doc ->
            if (doc != null){
               var data = doc.toObject(StockIndexes::class.java)
                data?.let {
                    indexObj = data
                }
            }
        }
        index.await()
        return indexObj
    }
}