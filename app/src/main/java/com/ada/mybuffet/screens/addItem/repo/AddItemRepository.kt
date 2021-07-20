package com.ada.mybuffet.screens.addItem.repo

import android.util.Log
import androidx.work.Data
import com.ada.mybuffet.repo.FinnhubApi
import com.ada.mybuffet.screens.detailShare.model.DividendItem
import com.ada.mybuffet.screens.detailShare.model.FeeItem
import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.detailShare.model.SaleItem
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.DatabaseException
import com.ada.mybuffet.utils.InvalidSalePurchaseBalanceException
import com.ada.mybuffet.utils.Resource
import com.ada.mybuffet.utils.StockNotFound
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.net.URLEncoder
import java.util.*
import kotlin.Exception

/**
 * @author Paul Pfisterer
 * Add Item Repository
 */
class AddItemRepository : IAddItemRepository {
    //firestore instance
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    //firestore user
    private val user = FirebaseAuth.getInstance().currentUser


    /**
     * Adds an item to the according collection of the database
     * The type of the item can be purchase, sale, fee or dividend
     * The id of the stock is passed
     */
    override suspend fun <T : Any> addItem(
        stockItem: ShareItem,
        item: T
    ): Resource<T> {
        //get user id
        val userid = user!!.uid
        //get item id
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
        //update the stock overview
        try {
            updateStock(shareItem = stockItem, item = item)
        } catch (e: Exception) {
            //If there is an error, return
            return Resource.Failure(e)
        }
        //add the item with the id to the collection with the path name
        val docRef = firestore.collection("users").document(userid).collection("shares")
            .document(stockItem.shareItemId).collection(pathName)
        docRef.add(item).await()
        return Resource.Success(item)
    }

    /**
     * Adds a purchase to the purchase collection of the database.
     * The id is unknown. Instead the symbol of the stock is passed.
     * First it is checked whether the stock symbol exists with the Finnhub API Service
     * If not, an Resource.Failure(e) is returned
     * Else it is checked whether the stock already exists in the db for the user
     * If yes, the purchase is added to this stock
     * If no, the stock is created in the db and the purchase is added to it
     */
    override suspend fun addPurchaseWithoutId(
        stockSymbol: String,
        purchase: Purchase
    ): Resource<Purchase> {
        try {
            //Search for Stock with FinnhubAPI Service
            val shareItem = getStock(stockSymbol, purchase)
            //Check if stock already exists in Database
            val existingShareItem = getDocument(shareItem.stockSymbol)
            //If the stock exists in the database, add the purchase to the stock with the shareItem
            //returned by getDocument()
            return if (existingShareItem != null) {
                addItem(existingShareItem, purchase)
            } else {
                //Create a new Stock in the database with the shareItem returned by getStock()
                val stockId = createStockEntry(shareItem)
                //Set the id of the shareItem with the id returned by createStockEntry()
                shareItem.shareItemId = stockId
                //Add the purchase to the stock with the shareItem
                addItem(shareItem, purchase)
            }
        } catch (e: Exception) {
            //no result for the stock from the Finnhub API Service
            return Resource.Failure(e)
        }
    }

    /**
     * Checks if a stock with the passed symbol already exists in the db of the user
     * If yes, a ShareItem of the stock is returned
     * If no, null is returned
     */
    private suspend fun getDocument(stockSymbol: String): ShareItem? {
        //Get the user id
        val userid = user!!.uid
        //Get reference to document with where clause
        val docRef = firestore.collection("users").document(userid).collection("shares")
            .whereEqualTo("stockSymbol", stockSymbol)
        val dataSnapshot = docRef.get().await()
        //Check if there is an result, to determine whether an entry in the db exists
        return if (dataSnapshot.documents.isNotEmpty()) {
            dataSnapshot.documents[0].toObject(ShareItem::class.java)
        } else {
            null
        }
    }

    /**
     * Creates a new stock entry in the db of the user, with the shareItem passed.
     * Returns the id of the newly created stockEntry
     */
    private suspend fun createStockEntry(shareItem: ShareItem): String {
        val userid = user!!.uid
        val result =
            firestore.collection("users").document(userid).collection("shares").add(shareItem)
        return result.await().id
    }

    /**
     * Searches for a stock with the passed stock symbol with the Finnhub API Service.
     * A shareItem representing the stock is returned
     * If the stock is not found, an Exception is thrown
     */
    private suspend fun getStock(symbol: String, item: Purchase): ShareItem {
        try {
            //Search for Stock with symbol
            val stockList = FinnhubApi.retrofitService.getName(symbol)
            if (stockList.count == 0) {
                throw StockNotFound("A stock with this symbol could not be found")
            }
            //If found, get the price and create a ShareITem
            val stockResult = stockList.result[0]
            val price = FinnhubApi.retrofitService.getCurrentPrice(stockResult.symbol)
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
            throw DatabaseException("A network error occurred")
        }
    }

    /**
     * This function updates the values stored in the overview stock document in the db, whenever
     * a change in one of the collections(sale, fee, dividend, purchase) occurs
     * In case of a creation of a sale, it is also checked if its valid (less sales than purchases)
     * if not, an Exception is thrown
     */
    private suspend fun updateStock(
        shareItem: ShareItem,
        item: Any
    ) {
        //get user id
        val userid = user!!.uid
        //get reference to the document with the stockId
        val stockDocRef = firestore.collection("users").document(userid).collection("shares")
            .document(shareItem.shareItemId)
        //Get the document of the stock and wait for it
        val dataSnapshot = stockDocRef.get().await()
        //Cast to shareItem (Representing the stock)
        val stockItem = dataSnapshot.toObject(ShareItem::class.java)
            ?: throw DatabaseException("Item could not be added")

        //Depending on the type of the item, different values of the stock overview are updated
        when (item) {
            is Purchase -> {
                val purchase = item as Purchase
                //new values are calculated
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
                    String.format(Locale.ENGLISH, "%.2f", newTotalInvestment),
                    "totalHoldings",
                    String.format(Locale.ENGLISH, "%.2f", newTotalHoldings),
                    "totalShareNumber",
                    String.format(Locale.ENGLISH, "%.2f", newShareNumber),
                    "totalFees",
                    String.format(Locale.ENGLISH, "%.2f", newTotalFees),
                    "totalPurchaseNumber",
                    String.format(Locale.ENGLISH, "%.2f", newTotalPurchaseNumber),
                    "totalPurchaseAmount",
                    String.format(Locale.ENGLISH, "%.2f", newTotalPurchaseAmount),
                )
            }
            is SaleItem -> {
                //If a sale is added, it is checked, whether there would be more sales
                //than purchases (invalid state). If thats the case, the creation is prevented, an
                //Exception is returned
                val sale = item as SaleItem
                var newShareNumber: Int = stockItem.totalShareNumber - sale.shareNumber
                if (newShareNumber < 0) {
                    throw InvalidSalePurchaseBalanceException("Sale could not be created, to few purchases exist")
                }
                //new values are calculated
                val newTotalHoldings: Double = stockItem.currentPrice.toDouble() * newShareNumber
                val newTotalInvestment: Double =
                    stockItem.totalInvestment.toDouble() + sale.fees.toDouble()
                val newTotalFees: Double = stockItem.totalFees.toDouble() + sale.fees.toDouble()
                val newTotalSaleNumber: Int =
                    stockItem.totalSaleNumber + sale.shareNumber
                val newTotalSaleAmount: Double =
                    stockItem.totalSaleAmount.toDouble() + sale.shareNumber * sale.sharePrice.toDouble()
                //the stock overview is updated in the db with the calculated values
                stockDocRef.update(
                    "totalHoldings",
                    String.format(Locale.ENGLISH, "%.2f", newTotalHoldings),
                    "totalShareNumber",
                    String.format(Locale.ENGLISH, "%.2f", newShareNumber),
                    "totalFees",
                    String.format(Locale.ENGLISH, "%.2f", newTotalFees),
                    "totalInvestment",
                    String.format(Locale.ENGLISH, "%.2f", newTotalInvestment),
                    "totalSaleNumber",
                    String.format(Locale.ENGLISH, "%.2f", newTotalSaleNumber),
                    "totalSaleAmount",
                    String.format(Locale.ENGLISH, "%.2f", newTotalSaleAmount),
                )
            }
            is FeeItem -> {
                val feeItem = item as FeeItem
                //new values are calculated
                val newTotalInvestment: Double =
                    stockItem.totalInvestment.toDouble() + feeItem.amount.toDouble()
                val newTotalFees: Double =
                    stockItem.totalFees.toDouble() + feeItem.amount.toDouble()
                //the stock overview is updated in the db with the calculated values
                stockDocRef.update(
                    "totalInvestment",
                    String.format(Locale.ENGLISH, "%.2f", newTotalInvestment),
                    "totalFees",
                    String.format(Locale.ENGLISH, "%.2f", newTotalFees),
                )
            }
            is DividendItem -> {
                val dividendItem = item as DividendItem
                //new values are calculated
                val newTotalInvestment: Double =
                    stockItem.totalDividends.toDouble() + dividendItem.amount.toDouble()
                //the stock overview is updated in the db with the calculated values
                stockDocRef.update(
                    "totalDividends",
                    String.format(Locale.ENGLISH, "%.2f", newTotalInvestment),
                )
            }
            else -> throw DatabaseException("Item could not be added")
        }
    }
}