package com.ada.mybuffet.screens.detailShare.repo

import android.util.Log
import com.ada.mybuffet.repo.SymbolPrice
import com.ada.mybuffet.screens.detailShare.model.DividendItem
import com.ada.mybuffet.screens.detailShare.model.FeeItem
import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.detailShare.model.SaleItem
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*

class ShareDetailRepository : IShareDetailRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val TAG = "SHARE_DETAIL_REPOSITORY"


    override suspend fun getPurchases(stockId: String): Flow<Resource<MutableList<Purchase>>> =
        callbackFlow {
            val userid = user!!.uid
            val docRef = firestore.collection("users").document(userid).collection("shares")
                .document(stockId).collection("purchases")
            val subscription = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                } else if (snapshot != null) {
                    Log.d(TAG, "Current data: available")

                    val purchaseList = mutableListOf<Purchase>()
                    val documents = snapshot.documents
                    documents.forEach { doc ->
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

    override suspend fun getSales(stockId: String): Flow<Resource<MutableList<SaleItem>>> =
        callbackFlow {
            val userid = user!!.uid
            val docRef = firestore.collection("users").document(userid).collection("shares")
                .document(stockId).collection("sales")
            val subscription = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                } else if (snapshot != null) {
                    Log.d(TAG, "Current data: available")

                    val saleList = mutableListOf<SaleItem>()
                    val documents = snapshot.documents
                    documents.forEach { doc ->
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

    override suspend fun getFees(stockId: String): Flow<Resource<MutableList<FeeItem>>> =
        callbackFlow {
            val userid = user!!.uid
            val docRef = firestore.collection("users").document(userid).collection("shares")
                .document(stockId).collection("fees")
            val subscription = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                } else if (snapshot != null) {
                    Log.d(TAG, "Current data: available")

                    val feeList = mutableListOf<FeeItem>()
                    val documents = snapshot.documents
                    documents.forEach { doc ->
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

    override suspend fun getDividends(stockId: String): Flow<Resource<MutableList<DividendItem>>> =
        callbackFlow {
            val userid = user!!.uid
            val docRef = firestore.collection("users").document(userid).collection("shares")
                .document(stockId).collection("dividends")
            val subscription = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                } else if (snapshot != null) {
                    Log.d(TAG, "Current data: available")

                    val dividendList = mutableListOf<DividendItem>()
                    val documents = snapshot.documents
                    documents.forEach { doc ->
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


    override suspend fun <T : Any> deleteItem(
        stockId: String,
        item: T
    ): Resource<T> {
        val userid = user!!.uid
        val itemId = when (item) {
            is Purchase -> item.id
            is SaleItem -> item.id
            is FeeItem -> item.id
            is DividendItem -> item.id
            else -> return Resource.Failure(Exception())
        }
        val pathName = when (item) {
            is Purchase -> "purchases"
            is SaleItem -> "sales"
            is FeeItem -> "fees"
            is DividendItem -> "dividends"
            else -> return Resource.Failure(Exception())
        }
        val result = updateStock(stockId, item, false)
        if(!result) {return  Resource.Failure(Exception())}
        val docRef = firestore.collection("users").document(userid).collection("shares")
            .document(stockId).collection(pathName).document(itemId)
        docRef.delete().await()
        return Resource.Success(item)
    }

    override suspend fun <T : Any> addItem(
        stockId: String,
        item: T
    ): Resource<T> {
        val userid = user!!.uid
        val itemId = when (item) {
            is Purchase -> item.id
            is SaleItem -> item.id
            is FeeItem -> item.id
            is DividendItem -> item.id
            else -> return Resource.Failure(Exception())
        }
        val pathName = when (item) {
            is Purchase -> "purchases"
            is SaleItem -> "sales"
            is FeeItem -> "fees"
            is DividendItem -> "dividends"
            else -> return Resource.Failure(Exception())
        }
        updateStock(stockId, item, true)
        val docRef = firestore.collection("users").document(userid).collection("shares")
            .document(stockId).collection(pathName).document(itemId)
        docRef.set(item).await()
        return Resource.Success(item)
    }

    private suspend fun updateStock(
        stockId: String,
        item: Any,
        added: Boolean
    ) : Boolean {
        val userid = user!!.uid
        val stockDocRef = firestore.collection("users").document(userid).collection("shares")
            .document(stockId)

        val dataSnapshot = stockDocRef.get().await()
        val stockItem = dataSnapshot.toObject(ShareItem::class.java) ?: return false

        when (item) {
            is Purchase -> {
                val purchase = item as Purchase
                if(!added) {
                    if((stockItem.totalPurchaseNumber - purchase.shareNumber) < stockItem.totalSaleNumber) {
                        return false
                    }
                }
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
                    newTotalSaleNumber.toInt(),
                    "totalSaleAmount",
                    newTotalSaleAmount.toString()
                )
            }
            is FeeItem -> {
                val feeItem = item as FeeItem
                val newTotalInvestment =
                    calculate(
                        stockItem.totalInvestment.toDouble(),
                        feeItem.amount.toDouble(),
                        added
                    )
                val newTotalFees: Double =
                    calculate(stockItem.totalFees.toDouble(), feeItem.amount.toDouble(), added)
                stockDocRef.update(
                    "totalInvestment",
                    newTotalInvestment.toString(),
                    "totalFees",
                    newTotalFees.toString()
                )
            }
            is DividendItem -> {
                val dividendItem = item as DividendItem
                val newTotalInvestment =
                    calculate(
                        stockItem.totalDividends.toDouble(),
                        dividendItem.amount.toDouble(),
                        added
                    )
                stockDocRef.update(
                    "totalDividends",
                    newTotalInvestment.toString(),
                )
            }
            else -> return false
        }
        return true
    }

    override suspend fun updatePriceInDB(stockId: String, price: SymbolPrice) {
        val currentPrice = price.c;
        val previousPrice = price.pc;
        val pricePercentage = (1-(previousPrice/currentPrice))*100
        val userid = user!!.uid
        val stockDocRef = firestore.collection("users").document(userid).collection("shares")
            .document(stockId)
        val currentPriceStr = String.format(Locale.ENGLISH, "%.2f",currentPrice)
        val currentPricePercentStr = String.format(Locale.ENGLISH, "%.2f",pricePercentage)
        stockDocRef.update(
            "currentPrice",
            currentPriceStr,
            "currentPricePercent",
            currentPricePercentStr,
        )
    }

    private fun calculate(x: Double, y: Double, add: Boolean): Double {
        return if (add) {
            x + y
        } else {
            x - y
        }
    }
}