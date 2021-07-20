package com.ada.mybuffet.screens.detailShare.repo

import android.util.Log
import com.ada.mybuffet.repo.FinnhubApi
import com.ada.mybuffet.repo.SymbolPrice
import com.ada.mybuffet.screens.detailShare.model.DividendItem
import com.ada.mybuffet.screens.detailShare.model.FeeItem
import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.detailShare.model.SaleItem
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.DatabaseException
import com.ada.mybuffet.utils.InvalidSalePurchaseBalanceException
import com.ada.mybuffet.utils.Resource
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.*

/**
 * @author Paul Pfisterer
 * Share Detail Repository.
 */
class ShareDetailRepository : IShareDetailRepository {
    //firestore instance
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    //firestore user
    private val user = FirebaseAuth.getInstance().currentUser
    //tag for logging
    private val TAG = "SHARE_DETAIL_REPOSITORY"

    /**
     * Gets a list of all purchases from the db from the stock, with the passed id
     * Returns the list encapsulated in a Resource as Flow
     */
    override suspend fun getPurchases(stockId: String): Flow<Resource<MutableList<Purchase>>> =
        callbackFlow {
            //get user id
            val userid = user!!.uid
            //get reference to purchases in db
            val docRef = firestore.collection("users").document(userid).collection("shares")
                .document(stockId).collection("purchases")
            //get all purchases and add snapshot listener to listen for result
            val subscription = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                } else if (snapshot != null) {
                    Log.d(TAG, "Current data: available")
                    val purchaseList = mutableListOf<Purchase>()
                    val documents = snapshot.documents
                    documents.forEach { doc ->
                        //for each purchase returned, cast to Purchase and add it to list
                        val purchase = doc.toObject(Purchase::class.java)
                        if (purchase != null && purchase.sharePrice.isNotEmpty()) {
                            purchaseList.add(purchase)
                        }
                    }
                    // offer for flow (offer method is now deprecated, using trySend instead)
                    trySend(Resource.Success(purchaseList)).isSuccess
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
            // close flow channel if not in use to avoid leaks
            awaitClose {
                subscription.remove()
            }
        }

    /**
     * Gets a list of all sales from the db from the stock, with the passed id
     * Returns the list as Flow, encapsulated in a Resource
     */
    override suspend fun getSales(stockId: String): Flow<Resource<MutableList<SaleItem>>> =
        callbackFlow {
            //get user id
            val userid = user!!.uid
            //get reference to sales in db
            val docRef = firestore.collection("users").document(userid).collection("shares")
                .document(stockId).collection("sales")
            //get all sales and add snapshot listener to listen for result
            val subscription = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                } else if (snapshot != null) {
                    Log.d(TAG, "Current data: available")
                    val saleList = mutableListOf<SaleItem>()
                    val documents = snapshot.documents
                    documents.forEach { doc ->
                        //for each sale returned, cast to SaleItem and add it to list
                        val saleItem = doc.toObject(SaleItem::class.java)
                        if (saleItem != null && saleItem.sharePrice.isNotEmpty()) {
                            saleList.add(saleItem)
                        }
                    }
                    // offer for flow (offer method is now deprecated, using trySend instead)
                    trySend(Resource.Success(saleList)).isSuccess
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
            // close flow channel if not in use to avoid leaks
            awaitClose {
                subscription.remove()
            }
        }

    /**
     * Gets a list of all fees from the db from the stock, with the passed id
     * Returns the list as Flow, encapsulated in a Resource
     */
    override suspend fun getFees(stockId: String): Flow<Resource<MutableList<FeeItem>>> =
        callbackFlow {
            //get user id
            val userid = user!!.uid
            //get reference to fees in db
            val docRef = firestore.collection("users").document(userid).collection("shares")
                .document(stockId).collection("fees")
            //get all fees and add snapshot listener to listen for result
            val subscription = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                } else if (snapshot != null) {
                    Log.d(TAG, "Current data: available")
                    val feeList = mutableListOf<FeeItem>()
                    val documents = snapshot.documents
                    documents.forEach { doc ->
                        //for each fee returned, cast to FeeItem and add it to list
                        val feeItem = doc.toObject(FeeItem::class.java)
                        if (feeItem != null && feeItem.amount.isNotEmpty()) {
                            feeList.add(feeItem)
                        }
                    }
                    // offer for flow (offer method is now deprecated, using trySend instead)
                    trySend(Resource.Success(feeList)).isSuccess
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
            // close flow channel if not in use to avoid leaks
            awaitClose {
                subscription.remove()
            }
        }

    /**
     * Gets a list of all dividends from the db from the stock, with the passed id
     * Returns the list as Flow, encapsulated in a Resource
     */
    override suspend fun getDividends(stockId: String): Flow<Resource<MutableList<DividendItem>>> =
        callbackFlow {
            //get user id
            val userid = user!!.uid
            //get reference to dividends in db
            val docRef = firestore.collection("users").document(userid).collection("shares")
                .document(stockId).collection("dividends")
            //get all dividends and add snapshot listener to listen for result
            val subscription = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                } else if (snapshot != null) {
                    Log.d(TAG, "Current data: available")
                    val dividendList = mutableListOf<DividendItem>()
                    val documents = snapshot.documents
                    documents.forEach { doc ->
                        //for each dividend returned, cast to FeeItem and add it to list
                        val dividendItem = doc.toObject(DividendItem::class.java)
                        if (dividendItem != null && dividendItem.amount.isNotEmpty()) {
                            dividendList.add(dividendItem)
                        }
                    }
                    // offer for flow (offer method is now deprecated, using trySend instead)
                    trySend(Resource.Success(dividendList)).isSuccess
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
            // close flow channel if not in use to avoid leaks
            awaitClose {
                subscription.remove()
            }
        }

    /**
     * Gets the current price of a stock with the passed symbol
     */
    override suspend fun getCurrentPrice(symbol: String): SymbolPrice? {
        return try {
            FinnhubApi.retrofitService.getCurrentPrice(symbol)
        } catch (networkError: IOException) {
            null
        }
    }

    /**
     * Deletes an item from the db. The item can be a purchase, sale, fee or dividend.
     * Depending on the type, the item is deleted from the correct collection.
     */
    override suspend fun <T : Any> deleteItem(
        stockId: String,
        item: T
    ): Resource<T> {
        //get user id
        val userid = user!!.uid
        //get item id
        val itemId = when (item) {
            is Purchase -> item.id
            is SaleItem -> item.id
            is FeeItem -> item.id
            is DividendItem -> item.id
            else -> return Resource.Failure(Exception())
        }
        //set path name
        val pathName = when (item) {
            is Purchase -> "purchases"
            is SaleItem -> "sales"
            is FeeItem -> "fees"
            is DividendItem -> "dividends"
            else -> return Resource.Failure(Exception())
        }
        //update the stock overview
        try {
            updateStock(stockId, item, false)
        } catch (e: Exception) {
            //If there is an error, return
            return Resource.Failure(e)
        }

        //Else delete item with the id from the collection with the path name
        val docRef = firestore.collection("users").document(userid).collection("shares")
            .document(stockId).collection(pathName).document(itemId)
        docRef.delete().await()
        return Resource.Success(item)
    }

    /**
     * Adds an item to the db. The item can be a purchase, sale, fee or dividend.
     * Depending on the type, the item is added to the correct collection.
     * This is used for undoing a deletion
     */
    override suspend fun <T : Any> addItem(
        stockId: String,
        item: T
    ): Resource<T> {
        //get user id
        val userid = user!!.uid
        //get item id
        val itemId = when (item) {
            is Purchase -> item.id
            is SaleItem -> item.id
            is FeeItem -> item.id
            is DividendItem -> item.id
            else -> return Resource.Failure(Exception())
        }
        //get item path name
        val pathName = when (item) {
            is Purchase -> "purchases"
            is SaleItem -> "sales"
            is FeeItem -> "fees"
            is DividendItem -> "dividends"
            else -> return Resource.Failure(Exception())
        }
        //update the stock overview
        try {
            updateStock(stockId, item, true)
        } catch (e: Exception) {
            //If there is an error, return
            return Resource.Failure(e)
        }
        //add the item with the id to the collection with the path name
        val docRef = firestore.collection("users").document(userid).collection("shares")
            .document(stockId).collection(pathName).document(itemId)
        docRef.set(item).await()
        return Resource.Success(item)
    }

    /**
     * This function updates the values stored in the overview stock document in the db, whenever
     * a change in one of the collections(sale, fee, dividend, purchase) occurs
     * The added boolean, indicates whether the item was added or deleted and is passed to the
     * calculate function
     * In case of a deletion of a purchase, it is also checked if its valid (less sales than purchases)
     * if not, an Exception is thrown
     */
    private suspend fun updateStock(
        stockId: String,
        item: Any,
        added: Boolean
    ) {
        //get user id
        val userid = user!!.uid
        //get reference to the document with the stockId
        val stockDocRef = firestore.collection("users").document(userid).collection("shares")
            .document(stockId)
        //Get the document of the stock and wait for it
        val dataSnapshot = stockDocRef.get().await()
        //Cast to shareItem (Representing the stock)
        val stockItem = dataSnapshot.toObject(ShareItem::class.java)
            ?: throw DatabaseException("Item could not be added")

        //Depending on the type of the item, different values of the stock overview are updated
        when (item) {
            is Purchase -> {
                val purchase = item as Purchase
                //If a purchase is deleted, it is checked, whether there would be more sales
                //than purchases (invalid state). If thats the case, the deletion is prevented, an
                //Exception is returned
                if (!added) {
                    if ((stockItem.totalPurchaseNumber - purchase.shareNumber) < stockItem.totalSaleNumber) {
                        throw InvalidSalePurchaseBalanceException("Purchase could not be deleted, to many sales exist")
                    }
                }
                //new values are calculated
                val newShareNumber = calculate(
                    stockItem.totalShareNumber.toDouble(),
                    purchase.shareNumber.toDouble(),
                    added
                )
                val newTotalInvestment =
                    calculate(
                        calculate(
                            stockItem.totalInvestment.toDouble(),
                            purchase.shareNumber * purchase.sharePrice.toDouble(),
                            added
                        ), purchase.fees.toDouble(), added
                    )
                val newTotalHoldings = stockItem.currentPrice.toDouble() * newShareNumber
                val newTotalFees =
                    calculate(
                        stockItem.totalFees.toDouble(),
                        purchase.fees.toDouble(),
                        added
                    )
                val newTotalPurchaseNumber: Int =
                    calculate(
                        stockItem.totalPurchaseNumber.toDouble(),
                        purchase.shareNumber.toDouble(),
                        added
                    ).toInt()
                val newTotalPurchaseAmount: Double =
                    calculate(
                        stockItem.totalPurchaseAmount.toDouble(),
                        purchase.shareNumber * purchase.sharePrice.toDouble(),
                        added
                    )
                //the stock overview is updated in the db with the calculated values
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
                val sale = item as SaleItem
                //new values are calculated
                var newShareNumber = calculate(
                    stockItem.totalShareNumber.toDouble(),
                    sale.shareNumber.toDouble(),
                    !added
                )
                if (newShareNumber < 0) {
                    newShareNumber = 0.0
                }
                val newTotalInvestment =
                    calculate(
                        stockItem.totalInvestment.toDouble(), sale.fees.toDouble(), added
                    )
                val newTotalHoldings = stockItem.currentPrice.toDouble() * newShareNumber
                val newTotalFees =
                    calculate(
                        stockItem.totalFees.toDouble(),
                        sale.fees.toDouble(),
                        added
                    )
                val newTotalSaleNumber: Double =
                    calculate(
                        stockItem.totalSaleNumber.toDouble(),
                        sale.shareNumber.toDouble(),
                        added
                    )
                val newTotalSaleAmount: Double =
                    calculate(
                        stockItem.totalSaleAmount.toDouble(),
                        sale.shareNumber * sale.sharePrice.toDouble(),
                        added
                    )
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
                val newTotalInvestment =
                    calculate(
                        stockItem.totalInvestment.toDouble(),
                        feeItem.amount.toDouble(),
                        added
                    )
                val newTotalFees: Double =
                    calculate(stockItem.totalFees.toDouble(), feeItem.amount.toDouble(), added)
                //the stock overview is updated in the db with the calculated values
                stockDocRef.update(
                    "totalInvestment",
                    String.format(Locale.ENGLISH, "%.2f", newTotalInvestment),
                    "totalFees",
                    String.format(Locale.ENGLISH, "%.2f", newTotalFees)
                )
            }
            is DividendItem -> {
                val dividendItem = item as DividendItem
                //new values are calculated
                val newTotalDividends =
                    calculate(
                        stockItem.totalDividends.toDouble(),
                        dividendItem.amount.toDouble(),
                        added
                    )
                //the stock overview is updated in the db with the calculated values
                stockDocRef.update(
                    "totalDividends",
                    String.format(Locale.ENGLISH, "%.2f", newTotalDividends)
                )
            }
            else -> throw DatabaseException("Item could not be added")
        }
    }

    /**
     * Helper function, that adds or subtracts value y from x, depending on the value
     * passed in add
     */
    private fun calculate(x: Double, y: Double, add: Boolean): Double {
        return if (add) {
            x + y
        } else {
            x - y
        }
    }

    /**
     * This function updates the price of the stock in the db.
     * The price and the price-percentage of the stock with the stockId are set to
     * the values stored in the SymbolPrice
     */
    override suspend fun updatePriceInDB(stockId: String, price: SymbolPrice) {
        //prices are extracted from the symbol price
        val currentPrice = price.c;
        val previousPrice = price.pc;
        val pricePercentage = (1 - (previousPrice / currentPrice)) * 100
        //user id is set
        val userid = user!!.uid
        //reference to the stock is set
        val stockDocRef = firestore.collection("users").document(userid).collection("shares")
            .document(stockId)
        //prices are formatted
        val currentPriceStr = String.format(Locale.ENGLISH, "%.2f", currentPrice)
        val currentPricePercentStr = String.format(Locale.ENGLISH, "%.2f", pricePercentage)
        //the stock overview is updated in the
        stockDocRef.update(
            "currentPrice",
            currentPriceStr,
            "currentPricePercent",
            currentPricePercentStr,
        )
    }
}