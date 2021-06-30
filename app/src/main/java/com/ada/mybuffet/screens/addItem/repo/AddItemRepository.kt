package com.ada.mybuffet.screens.addItem.repo

import android.util.Log
import com.ada.mybuffet.repo.FinnhubApi
import com.ada.mybuffet.screens.detailShare.model.DividendItem
import com.ada.mybuffet.screens.detailShare.model.FeeItem
import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.detailShare.model.SaleItem
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.IOException
import kotlin.Exception

class AddItemRepository : IAddItemRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val TAG = "ADD_ITEM_REPOSITORY"


    override suspend fun <T : Any> addItem(
        stockId: String,
        item: T
    ): Resource<T> {
        val userid = user!!.uid
        val pathName = when (item) {
            is Purchase -> "purchases"
            is SaleItem -> "sales"
            is FeeItem -> "fees"
            is DividendItem -> "dividends"
            else -> return Resource.Failure(Exception())
        }
        val docRef = firestore.collection("users").document(userid).collection("shares")
            .document(stockId).collection(pathName)
        docRef.add(item).await()
        return Resource.Success(item)
    }


    override suspend fun addItemWithoutId(
        stockSymbol: String,
        item: Purchase
    ): Resource<Purchase> {

        //Check if document exists
        val documentId = getDocumentId(stockSymbol)
        if (documentId != null) {
            return addItem(documentId, item)
        }

        //Search for Stock with finhub
        val stockItem = getStock(stockSymbol, item)
        if (stockItem != null) {
            val stockId = createStockEntry(stockItem)
            return addItem(stockId, item)
        }
        //no result
        return Resource.Failure(Exception())
    }

    private suspend fun getDocumentId(stockSymbol: String): String? {
        val userid = user!!.uid

        val docRef = firestore.collection("users").document(userid).collection("shares")
            .whereEqualTo("stockSymbol", stockSymbol)
        val dataSnapshot = docRef.get().await()
        return if (dataSnapshot.documents.isNotEmpty()) {
            dataSnapshot.documents[0].id
        } else {
            null
        }
    }

    private suspend fun createStockEntry(shareItem: ShareItem): String {
        val userid = user!!.uid
        val result =
            firestore.collection("users").document(userid).collection("shares").add(shareItem)
        return result.await().id
    }

    private suspend fun getStock(symbol: String, item: Purchase): ShareItem? {
        try {
            //Search for Stock with symbol
            val getNameUrl =
                "https://finnhub.io/api/v1/search?q=${symbol}&token=sandbox_c2vgcniad3i9mrpv9cn0"
            val stockList = FinnhubApi.retrofitService.getName(getNameUrl)
            if (stockList.count == 0) {
                return null
            }
            //If found, get the price and create a ShareITem
            val stockResult = stockList.result[0]
            val getPriceUrl =
                "https://finnhub.io/api/v1/quote?symbol=${stockResult.symbol}&token=sandbox_c2vgcniad3i9mrpv9cn0"
            val price = FinnhubApi.retrofitService.getCurrentPrice(getPriceUrl)
            val totalInvestment =
                item.fees.toDouble() + (item.shareNumber * item.sharePrice.toDouble())
            val totalHolding = (item.shareNumber * item.sharePrice.toDouble())
            return ShareItem(
                stockSymbol = stockResult.symbol,
                stockName = stockResult.description,
                currentPrice = price.c.toString(),
                currentPricePercent = "0",
                totalDividends = "0",
                totalHoldings = totalHolding.toString(),
                totalFees = item.fees.toString(),
                totalInvestment = totalInvestment.toString()
            )

        } catch (networkError: IOException) {
            Log.i("API_ADD_STOCK", "fetchNews Error with code: ${networkError.message}")
            return null;
        }
    }
}