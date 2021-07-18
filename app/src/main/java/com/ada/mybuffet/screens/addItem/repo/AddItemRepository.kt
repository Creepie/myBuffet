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
import java.util.*
import kotlin.Exception

class AddItemRepository : IAddItemRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val TAG = "ADD_ITEM_REPOSITORY"


    override suspend fun <T : Any> addItem(
        stockItem: ShareItem,
        item: T
    ): Resource<T> {
        val userid = user!!.uid
        val pathName = when (item) {
            is Purchase -> {
                "purchases"
            }
            is SaleItem -> {
                "sales"
            }
            is FeeItem -> {
                "fees"
            }
            is DividendItem -> {
                "dividends"
            }
            else -> return Resource.Failure(Exception())
        }
        updateStock(shareItem = stockItem, item = item)
        val docRef = firestore.collection("users").document(userid).collection("shares")
            .document(stockItem.shareItemId).collection(pathName)
        docRef.add(item).await()
        return Resource.Success(item)
    }


    override suspend fun addItemWithoutId(
        stockSymbol: String,
        item: Purchase
    ): Resource<Purchase> {
        //Search for Stock with finhub
        val stockItem = getStock(stockSymbol, item)
        if (stockItem != null) {
            //Check if document exists
            val stock = getDocument(stockItem.stockSymbol)
            return if (stock != null) {
                addItem(stock, item)
            } else {
                val stockId = createStockEntry(stockItem)
                stockItem.shareItemId = stockId
                addItem(stockItem, item)
            }
        }
        //no result
        return Resource.Failure(Exception())
    }

    private suspend fun getDocument(stockSymbol: String): ShareItem? {
        val userid = user!!.uid

        val docRef = firestore.collection("users").document(userid).collection("shares")
            .whereEqualTo("stockSymbol", stockSymbol)
        val dataSnapshot = docRef.get().await()
        return if (dataSnapshot.documents.isNotEmpty()) {
            dataSnapshot.documents[0].toObject(ShareItem::class.java)
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
            val currentPrice = price.c
            val previousPrice = price.pc
            val pricePercentage = (1 - (previousPrice / currentPrice)) * 100
            val percentageString = "%.2f".format(Locale.ENGLISH, pricePercentage)
            val totalInvestment =
                item.fees.toDouble() + (item.shareNumber * item.sharePrice.toDouble())
            val totalHolding = (item.shareNumber * currentPrice)
            return ShareItem(
                stockSymbol = stockResult.symbol,
                stockName = stockResult.description,
                currentPrice = currentPrice.toString(),
                currentPricePercent = percentageString,
                totalDividends = "0",
                totalHoldings = "0",
                totalFees = "0",
                totalInvestment = "0",
            )

        } catch (networkError: IOException) {
            Log.i("API_ADD_STOCK", "fetchNews Error with code: ${networkError.message}")
            return null;
        }
    }

    private suspend fun updateStock(
        shareItem: ShareItem,
        item: Any
    ) {
        val userid = user!!.uid
        val stockDocRef = firestore.collection("users").document(userid).collection("shares")
            .document(shareItem.shareItemId)

        val dataSnapshot = stockDocRef.get().await()
        val stockItem = dataSnapshot.toObject(ShareItem::class.java) ?: return

        when (item) {
            is Purchase -> {
                val purchase = item as Purchase
                val newShareNumber: Double =
                    stockItem.totalShareNumber.toDouble() + purchase.shareNumber
                val newTotalInvestment: Double =
                    stockItem.totalInvestment.toDouble() + purchase.shareNumber * purchase.sharePrice.toDouble() + purchase.fees.toDouble()
                val newTotalHoldings: Double = stockItem.currentPrice.toDouble() * newShareNumber
                val newTotalFees: Double = stockItem.totalFees.toDouble() + purchase.fees.toDouble()
                val newTotalPurchaseNumber: Int =
                    stockItem.totalPurchaseNumber + purchase.shareNumber
                val newTotalPurchaseAmount: Double =
                    stockItem.totalPurchaseAmount.toDouble() + purchase.shareNumber * purchase.sharePrice.toDouble()
                stockDocRef.update(
                    "totalInvestment",
                    newTotalInvestment.toString(),
                    "totalHoldings",
                    newTotalHoldings.toString(),
                    "totalShareNumber",
                    newShareNumber,
                    "totalFees",
                    newTotalFees.toString(),
                    "totalPurchaseNumber",
                    newTotalPurchaseNumber,
                    "totalPurchaseAmount",
                    newTotalPurchaseAmount.toString()
                )
            }
            is SaleItem -> {
                val sale = item as SaleItem
                var newShareNumber: Int = stockItem.totalShareNumber - sale.shareNumber
                if (newShareNumber < 0) {
                    newShareNumber = 0
                }
                val newTotalHoldings: Double = stockItem.currentPrice.toDouble() * newShareNumber
                val newTotalInvestment: Double =
                    stockItem.totalInvestment.toDouble() + sale.fees.toDouble()
                val newTotalFees: Double = stockItem.totalFees.toDouble() + sale.fees.toDouble()
                val newTotalSaleNumber: Int =
                    stockItem.totalSaleNumber + sale.shareNumber
                val newTotalSaleAmount: Double =
                    stockItem.totalSaleAmount.toDouble() + sale.shareNumber * sale.sharePrice.toDouble()
                stockDocRef.update(
                    "totalHoldings",
                    newTotalHoldings.toString(),
                    "totalShareNumber",
                    newShareNumber,
                    "totalFees",
                    newTotalFees.toString(),
                    "totalInvestment",
                    newTotalInvestment.toString(),
                    "totalSaleNumber",
                    newTotalSaleNumber,
                    "totalSaleAmount",
                    newTotalSaleAmount.toString()
                    )
            }
            is FeeItem -> {
                val feeItem = item as FeeItem
                val newTotalInvestment: Double =
                    stockItem.totalInvestment.toDouble() + feeItem.amount.toDouble()
                val newTotalFees: Double =
                    stockItem.totalFees.toDouble() + feeItem.amount.toDouble()
                stockDocRef.update(
                    "totalInvestment",
                    newTotalInvestment.toString(),
                    "totalFees",
                    newTotalFees.toString()
                )
            }
            is DividendItem -> {
                val dividendItem = item as DividendItem
                val newTotalInvestment: Double =
                    stockItem.totalDividends.toDouble() + dividendItem.amount.toDouble()
                stockDocRef.update(
                    "totalDividends",
                    newTotalInvestment.toString(),
                )
            }
            else -> return
        }
    }
}