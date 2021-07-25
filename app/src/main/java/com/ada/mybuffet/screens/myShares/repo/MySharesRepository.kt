package com.ada.mybuffet.screens.myShares.repo

import android.util.Log
import com.ada.mybuffet.screens.detailShare.model.OverviewData
import com.ada.mybuffet.screens.myShares.model.PortfolioValueByDate
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import com.ada.mybuffet.utils.StockCalculationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.ArrayList

class MySharesRepository : IMySharesRepository {

    private val TAG = "MY_SHARES_REPOSITORY"
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        var shareItemStringList: ArrayList<String> = arrayListOf()
    }

    override suspend fun getShareItemsFromDB(): Flow<Resource<MutableList<ShareItem>>> =
        callbackFlow {
            // create reference to the collection in firestore
            val userid = FirebaseAuth.getInstance().currentUser!!.uid
            val docRef = firestore.collection("users").document(userid).collection("shares")

            // create subscription which listens to database changes
            val subscription = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                } else if (snapshot != null) {
                    Log.d(TAG, "Listen successful")

                    val shareItemList = mutableListOf<ShareItem>()
                    val documents = snapshot.documents
                    documents.forEach { doc ->
                        val shareItem = doc.toObject(ShareItem::class.java)
                        if (shareItem != null) {
                            shareItemList.add(shareItem)
                            shareItemStringList.add(shareItem.stockSymbol)
                        }
                    }

                    // offer for flow (offer method is now deprecated, using trySend instead)
                    trySend(Resource.Success(shareItemList)).isSuccess
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }

            // close flow channel if not in use to avoid leaks
            awaitClose {
                subscription.remove()
            }
        }

    override suspend fun getShareItemsAsListFromDB(): Resource<MutableList<ShareItem>> {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return Resource.Failure(
            IllegalAccessException()
        )

        var shareItemList = mutableListOf<ShareItem>()
        var returnValue: Resource<MutableList<ShareItem>> = Resource.Failure(IOException())

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
                    }
                }
                returnValue = Resource.Success<MutableList<ShareItem>>(shareItemList)
            }
            .addOnFailureListener {
                // could return a failure resource here
                Log.d(TAG, "Could not retrieve share items as list")
                returnValue = Resource.Failure(IOException())
            }

        sharesResults.await()
        return returnValue
    }

    /**
     * @author Pinsker Harald, Paul Pfisterer
     * Returns a map with all values need for the profit/loss overview of the whole portfolio
     * All stock overview documents of the user are gotten from the database
     * For each stock, a OverviewData object is created. With the OverviewData, the profit
     * and the value change of the current holdings is calculated.
     * The values of each stock are added and returned as a map
     */
    override suspend fun getProfitLossOverviewDataFromDB(): Flow<Resource<HashMap<String, BigDecimal>>> =
        callbackFlow {
            // create reference to the collection in firestore
            val userid = FirebaseAuth.getInstance().currentUser!!.uid
            val docRef = firestore.collection("users").document(userid).collection("shares")

            // create subscription which listens to database changes
            val subscription = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                } else if (snapshot != null) {
                    Log.d(TAG, "Listen successful")

                    var profitLossOverviewData = hashMapOf<String, BigDecimal>(
                        "totalProfit" to BigDecimal.ZERO,
                        "profitPercentage" to BigDecimal.ZERO,
                        "exchangeProfitLoss" to BigDecimal.ZERO,
                        "dividendProfit" to BigDecimal.ZERO,
                        "totalInvestment" to BigDecimal.ZERO,
                        "fees" to BigDecimal.ZERO
                    )

                    val documents = snapshot.documents
                    var profit = 0.0
                    var exchangeBalance = 0.0
                    var totalInvestment = 0.0
                    documents.forEach { doc ->
                        val totalHoldingsStr = doc.getString("totalHoldings")
                        val totalInvestmentStr = doc.getString("totalInvestment")
                        val totalFeesStr = doc.getString("totalFees")
                        val totalDividendsStr = doc.getString("totalDividends")
                        val totalPurchaseNumberStr = doc.getDouble("totalPurchaseNumber")
                        val totalPurchaseAmountStr = doc.getString("totalPurchaseAmount")
                        val totalSaleNumberStr = doc.getDouble("totalSaleNumber")
                        val totalSaleAmountStr = doc.getString("totalSaleAmount")
                        val currentWorthStr = doc.getString("currentPrice")

                        if (totalHoldingsStr != null
                            && totalInvestmentStr != null
                            && totalFeesStr != null
                            && totalDividendsStr != null
                            && totalPurchaseNumberStr != null
                            && totalPurchaseAmountStr != null
                            && totalSaleNumberStr != null
                            && totalSaleAmountStr != null
                            && currentWorthStr != null
                        ) {
                            val totalFees = BigDecimal(totalFeesStr)
                            val totalDividends = BigDecimal(totalDividendsStr)

                            profitLossOverviewData["dividendProfit"] =
                                profitLossOverviewData["dividendProfit"]!!.plus(totalDividends)
                            profitLossOverviewData["fees"] =
                                profitLossOverviewData["fees"]!!.plus(totalFees)

                            val overviewData = OverviewData(
                                purchaseCount = totalPurchaseNumberStr.toInt(),
                                purchaseSum = totalPurchaseAmountStr.toDouble(),
                                saleCount = totalSaleNumberStr.toInt(),
                                saleSum = totalSaleAmountStr.toDouble(),
                                feeSum = totalFeesStr.toDouble(),
                                dividendSum = totalDividendsStr.toDouble(),
                                currentWorth = currentWorthStr.toDouble()
                            )
                            totalInvestment += totalInvestmentStr.toDouble()
                            exchangeBalance += StockCalculationUtils.calculateExchangeBalance(overviewData)
                            profit += StockCalculationUtils.calculateProfit(overviewData)
                        }
                    }

                    val profitPercentage = StockCalculationUtils.getProfitPercentage(totalInvestment, profit)

                    profitLossOverviewData["totalProfit"] = profit.toBigDecimal()
                    profitLossOverviewData["profitPercentage"] = profitPercentage.toBigDecimal()
                    profitLossOverviewData["exchangeProfitLoss"] = exchangeBalance.toBigDecimal()

                    // offer for flow (offer method is now deprecated, using trySend instead)
                    trySend(Resource.Success(profitLossOverviewData)).isSuccess
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }

            // close flow channel if not in use to avoid leaks
            awaitClose {
                subscription.remove()
            }
        }

    override suspend fun getTotalPortfolioValueHistoryFromDB(): Flow<Resource<MutableList<PortfolioValueByDate>>> =
        callbackFlow {
            // create reference to the collection in firestore
            val userid = FirebaseAuth.getInstance().currentUser!!.uid
            val docRef =
                firestore.collection("users").document(userid).collection("portfolioValueHistory")

            // create subscription which listens to database changes
            val subscription = docRef
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                    } else if (snapshot != null) {
                        Log.d(TAG, "Listen successful")

                        val portfolioValueList = mutableListOf<PortfolioValueByDate>()
                        val documents = snapshot.documents
                        documents.forEach { doc ->
                            val portfolioValueItem = doc.toObject(PortfolioValueByDate::class.java)
                            if (portfolioValueItem != null) {
                                portfolioValueList.add(portfolioValueItem)
                            }
                        }

                        // offer for flow (offer method is now deprecated, using trySend instead)
                        trySend(Resource.Success(portfolioValueList)).isSuccess
                    } else {
                        Log.d(TAG, "Current data: null")
                    }
                }

            // close flow channel if not in use to avoid leaks
            awaitClose {
                subscription.remove()
            }
        }

    private suspend fun oldGetProfitLossOverviewDataFromDB(): Flow<Resource<HashMap<String, BigDecimal>>> = callbackFlow {
        // create reference to the collection in firestore
        val userid = FirebaseAuth.getInstance().currentUser!!.uid
        val docRef = firestore.collection("users").document(userid).collection("shares")

        // create subscription which listens to database changes
        val subscription = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
            } else if (snapshot != null) {
                Log.d(TAG, "Listen successful")

                var profitLossOverviewData = hashMapOf<String, BigDecimal>(
                    "totalProfit" to BigDecimal.ZERO,
                    "profitPercentage" to BigDecimal.ZERO,
                    "exchangeProfitLoss" to BigDecimal.ZERO,
                    "dividendProfit" to BigDecimal.ZERO,
                    "totalInvestment" to BigDecimal.ZERO,
                    "fees" to BigDecimal.ZERO
                )

                val documents = snapshot.documents
                documents.forEach { doc ->
                    val totalHoldingsStr = doc.getString("totalHoldings")
                    val totalInvestmentStr = doc.getString("totalInvestment")
                    val totalFeesStr = doc.getString("totalFees")
                    val totalDividendsStr = doc.getString("totalDividends")

                    if (totalHoldingsStr != null && totalInvestmentStr != null && totalFeesStr != null && totalDividendsStr != null) {
                        val totalHoldings = BigDecimal(totalHoldingsStr)
                        val totalInvestment = BigDecimal(totalInvestmentStr)
                        val totalFees = BigDecimal(totalFeesStr)
                        val totalDividends = BigDecimal(totalDividendsStr)

                        val totalProfit = totalHoldings.plus(totalDividends).minus(totalInvestment).minus(totalFees)
                        val exchangeProfitLoss = totalHoldings.minus(totalInvestment)

                        profitLossOverviewData["totalProfit"] = profitLossOverviewData["totalProfit"]!!.plus(totalProfit)
                        profitLossOverviewData["exchangeProfitLoss"] = profitLossOverviewData["exchangeProfitLoss"]!!.plus(exchangeProfitLoss)
                        profitLossOverviewData["dividendProfit"] = profitLossOverviewData["dividendProfit"]!!.plus(totalDividends)
                        profitLossOverviewData["fees"] = profitLossOverviewData["fees"]!!.plus(totalFees)
                        profitLossOverviewData["totalInvestment"] = profitLossOverviewData["totalInvestment"]!!.plus(totalInvestment)
                    }
                }

                if (profitLossOverviewData["totalInvestment"] != BigDecimal.ZERO) {
                    val profitPercentage = profitLossOverviewData["totalProfit"]!!.multiply(BigDecimal(100)).divide(profitLossOverviewData["totalInvestment"]!!, 2, RoundingMode.HALF_UP)
                    profitLossOverviewData["profitPercentage"] = profitPercentage
                }


                // offer for flow (offer method is now deprecated, using trySend instead)
                trySend(Resource.Success(profitLossOverviewData)).isSuccess
            } else {
                Log.d(TAG, "Current data: null")
            }
        }

        // close flow channel if not in use to avoid leaks
        awaitClose{
            subscription.remove()
        }
    }
}