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

/**
 * @author Mario Eberth
 * this worker creates shares from the stocks and save theme on Firebase
 * for one share we need 3 api calls > so need to schedule the work for more shares
 */
class RefreshStockShareWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    val firestore = Firebase.firestore
    val model = StocksOverviewModel()

    companion object {
        const val WORK_NAME = "com.ada.mybuffet.repo.worker.RefreshStockShareWorker.kt"
    }

    /**
     * this method is called from the worker in the background every x minutes
     * every x minutes this method makes some api calls to get x shares and save theme on firebase
     * x shares is calculated to don't come over the 60 api calls / min
     */
    override suspend fun doWork(): Result {
        Log.i("WORKER_RUNNING", "Worker load shares is running")
        try {
            //get the current start index from firebase db
            var startIndexes = getStartIndex()
            //get the stock list from firebase
            var list = getStockListFromFirebase()
            //calc the endIndex (check out of bounds)
            var endIndex = calcEndIndex(startIndexes, list)
            //load the shares from the model
            var data = model.loadShares(startIndexes, endIndex, list)
            //save shares to firebase
            saveSharesToFirebase(data, list[startIndexes.stockIndex].symbol)
            //calc the new start Index for the next time
            var newIndex = calcStartIndex(endIndex, list)
            //save the new start index on firebase (overwrite the old one)
            saveStartIndexToFirebase(newIndex)
        } catch (e: HttpException) {
            Log.i("WORKER_RUNNING", "Worker load shares on error")
            return Result.retry()
        }
        Log.i("WORKER_RUNNING", "Worker load shares on success")
        return Result.success()
    }

    /**
     * this method is to calc the right end index for the api calls
     * check if out of bounds if yes > set to maximum if not set endIndex
     * @param startIndexes is the current start index
     * @param list is the list of indexSymbols where each contains a list of share symbols in it
     * @return the new end index for the next api call
     * @see StockIndexes
     */
    private fun calcEndIndex(startIndexes: StockIndexes, list: ArrayList<IndexSymbols>): StockIndexes{
        //todo calc end Index for index and shares
        var endIndex = StockIndexes(startIndexes.stockIndex, startIndexes.shareIndex)
        if (startIndexes.shareIndex + 5 >= list[startIndexes.stockIndex].constituents.size){
          endIndex.shareIndex = list[startIndexes.stockIndex].constituents.size-1
        } else {
          endIndex.shareIndex += 5
        }
        return endIndex
    }

    /**
     * this method is to calc the new startIndex for the next api call
     * @param startIndexes is the current startIndex for the next api call
     * @param list is the list of IndexSymbols with a list of shares in it (constituents)
     * @return the new start Index (stock and share) for the next api call
     */
    private fun calcStartIndex(startIndexes: StockIndexes, list: ArrayList<IndexSymbols>): StockIndexes{

        if (startIndexes.shareIndex >= list[startIndexes.stockIndex].constituents.size-1){
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
     * @return the stockList
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
     * this method saves the shares on firebase
     * @param data is the list of StockShares
     * @param symbol is the symbol of the share
     */
    private fun saveSharesToFirebase(data: ArrayList<StockShare>, symbol: String) {
        for (share in data) {
            firestore.collection(symbol).document(share.symbol).set(share)
        }
    }

    /**
     * this method saves the new start index to firebase for the next api call
     * @param startIndexes is the new startIndex for the next api call
     */
    private fun saveStartIndexToFirebase(startIndexes: StockIndexes) {
        firestore.collection("stockIndexes").document("index").set(startIndexes)
    }

    /**
     * this method gets the start index from firebase
     * @return the start index from firebase if not on firebase return 0,0
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