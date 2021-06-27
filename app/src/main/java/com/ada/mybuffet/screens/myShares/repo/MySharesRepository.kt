package com.ada.mybuffet.screens.myShares.repo

import android.util.Log
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.math.BigDecimal
import java.math.RoundingMode

class MySharesRepository : IMySharesRepository {

    private val TAG = "MY_SHARES_REPOSITORY"
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser


    override suspend fun getShareItemsFromDB(): Flow<Resource<MutableList<ShareItem>>> = callbackFlow {
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
                    }
                }

                // offer for flow (offer method is now deprecated, using trySend instead)
                trySend(Resource.Success(shareItemList)).isSuccess
            } else {
                Log.d(TAG, "Current data: null")
            }
        }

        // close flow channel if not in use to avoid leaks
        awaitClose{
            subscription.remove()
        }
    }

    override suspend fun getProfitLossOverviewDataFromDB(): Flow<Resource<HashMap<String, BigDecimal>>> = callbackFlow {
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