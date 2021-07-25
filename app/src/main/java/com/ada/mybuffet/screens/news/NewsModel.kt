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

    val map = mapOf("DAX" to "^GDAXI", "Dow Jones" to "^DJI", "Nasdaq 100" to "^NDX", "Euro Stoxx 50" to "^STOXX50E" )

    //this is the list of all supported stocks
    var indexList = map.values.toList()
    val firestore = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth

    var urlList = arrayListOf<String>(
        "https://finnhub.io/api/v1/press-releases?symbol=" + shareItemStringList.get(0) +"&token=sandbox_c2vgcniad3i9mrpv9cn0",
        "https://finnhub.io/api/v1/press-releases?symbol=" + shareItemStringList.get(1) +"&token=sandbox_c2vgcniad3i9mrpv9cn0"
    )



    suspend fun loadAll(): ArrayList<SymbolPressResponse>{
        // list shows the newest share item at first
        shareItemStringList.reverse()

        var list = ArrayList<SymbolPressResponse>()
        var time = System.currentTimeMillis()
        val scopeList = mutableListOf<Deferred<Boolean?>>()


        for (i in 0..shareItemStringList.size-1){ // url in urlList
            var test = CoroutineScope(Dispatchers.IO).async {
                Log.d("LOG",(System.currentTimeMillis()-time).toString())
                var test = getNews(shareItemStringList.get(i)) //   url
                test?.let {
                    list.add(test)
                }
            }
            scopeList.add(test)
        }
        scopeList.awaitAll()
        return list
    }




    /**
     * this method loads Symbols from firebase db
     * @param symbol is the stock symbol we are looking for
     * @return a list of strings that contains the symbols of the stocks that the user has
     */
     suspend fun getShareItemsAsListFromDB(): Resource<MutableList<ShareItem>> {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return Resource.Failure(
            IllegalAccessException()
        )

        val shareItemList = mutableListOf<ShareItem>()
        var returnValue: Resource<MutableList<ShareItem>> = Resource.Failure(IOException())
        var symbolList = ArrayList<String>()

        // create reference to the collection in firestore
        val userid = currentUser.uid
        val docRef = firestore.collection("users").document(userid).collection("shares")


        // get data
        val sharesResults = docRef
            .get()
            .addOnSuccessListener { documents ->
                documents.forEach { doc ->
                    val shareItem = doc.toObject(ShareItem::class.java)
                    if (shareItem != null) {
                        shareItemList.add(shareItem)
                        symbolList.add(shareItem.stockSymbol)
                    }
                }
                returnValue = Resource.Success<MutableList<ShareItem>>(shareItemList)
            }
            .addOnFailureListener {
                // could return a failure resource here
                Log.d("TAG", "Could not retrieve share items as list")
                returnValue = Resource.Failure(IOException())
            }

        sharesResults.await()
        Log.d("TAG", symbolList.toString())
        return returnValue
    }


    /*
    suspend fun loadSharesFromFirebase(): ArrayList<String>{
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser!!.uid

        val list = ArrayList<SymbolPressResponse>()
        val listValues = ArrayList<String>()

        val docRef = firestore.collection("users").document(currentUser).collection("shares")

        // create subscription which listens to database changes
        val subscription = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
            } else if (snapshot != null) {
                Log.d("TAG", "Listen successful")

                val shareItemList = mutableListOf<ShareItem>()
                val documents = snapshot.documents
                documents.forEach { doc ->
                    val shareItem = doc.toObject(SymbolPressResponse::class.java)
                    if (shareItem != null) {
                        list.add(shareItem)
                    }
                }


            } else {
                Log.d("TAG", "Current data: null")
            }
        }


        return listValues
    }

     */



    suspend fun getNews(url: String) :SymbolPressResponse?{
            return try {
                FinnhubApi.retrofitService.getPressNews(url)
            } catch (networkError: IOException){
                Log.i("API","fetchNews Error with code: ${networkError.message}")
                null;
            }
    }
}